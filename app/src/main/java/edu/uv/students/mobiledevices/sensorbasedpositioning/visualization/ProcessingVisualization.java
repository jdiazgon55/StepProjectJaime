package edu.uv.students.mobiledevices.sensorbasedpositioning.visualization;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnPathChangedListener;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.Table;
import processing.data.TableRow;

import processing.core.*;


/**
 * Created by Fabian and Jaime on 03.05.2017.
 */

public class ProcessingVisualization extends PApplet implements OnPathChangedListener {
    Context context;
    SensorManager manager;
    Sensor sensor;
    AccelerometerListener listener;
    Table tabla, tablaLectura;
    int primeraEjecucion = 0;
    boolean acelerometroListo = false;
    int ST_DESACTIVADO = 0, ST_TRABAJANDO = 1, ST_NO_REINICIO = 2, ST_REINICIANDO = 3;
    MaquinaEstados mqEst;
    float ax, ay, az, norte = -10000.0f, rotacionActual, longitudPaso = 50.0f, milisegundosPaso = 0.0f;
    boolean takeScreenShot = false;
    int pasosGlobal = 0;
    boolean acabado = false;
    ArrayList<ArrayList<float[]>> matrizPasos = new ArrayList<ArrayList<float[]>>();
    ArrayList<float[]> pasoActual = new ArrayList<float[]>();
    float tiempoSiguientePaso = 100000.0f;
    PVector userPosition;
    int frames = 0, light = 55, colorChange = 5, fileNumber = 1;
    MovingBackground movingBackground;
    PGraphics pg;
    private boolean reiniciarMaxAceleracion = true;

    private float maxAceleracionY = 0.0f;
    int[] esta2 = new int[2];

    public void setup() {

        orientation(PORTRAIT);
        context = getActivity();
        manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new AccelerometerListener();
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        fill(0);
        stroke(0);
        strokeWeight(2);
        background(0);

        //Solo para la primera vez
        File folder = new File("//sdcard/CarpetaPasos/");
        folder.mkdirs();

        tabla = new Table();
        tablaLectura = loadTable("paso.csv");

        mqEst = new MaquinaEstados(ST_DESACTIVADO);
        mqEst.addState(ST_DESACTIVADO);
        mqEst.addState(ST_TRABAJANDO);
        mqEst.addState(ST_NO_REINICIO);
        mqEst.addState(ST_REINICIANDO);
        Boton botonPasos = new Boton(width/4.0f, height/14, width/2.5f, height/12.0f, "Calcular Pasos", 200);
        Boton botonReiniciar = new Boton(width/1.3f, height/14, width/2.5f, height/12.0f, "Reiniciar", 200);
        mqEst.btn2State(botonPasos, ST_DESACTIVADO);
        mqEst.btn2State(botonReiniciar, ST_NO_REINICIO);

        //Guarda en la matriz el conjunto de entrenamiento
        matrizPasos = guardarDatosMatriz();

        userPosition = new PVector(width/2.0f, height/2.0f);

        movingBackground = new MovingBackground();

        pg = createGraphics(width, height);

        //Pintamos por primera vez el fondo
        movingBackground.draw(userPosition);
    }

    public void draw() {

        mqEst.run();

        //Coge todos los estados activos
        esta2 = mqEst.EstadoDeBoton();

        if(esta2[0] == ST_TRABAJANDO){
            if (acelerometroListo)
            {
                //Used for saving the direction of the user to a PNG
                takeScreenShot = true;
                // Guardamos las aceleraciones y cuando tenemos listo el array, comprobamos si hay paso
                if(guardarAceleracionesArray()){
                    //Solo si ha habido un paso pintamos de nuevo
                    movingBackground.draw(userPosition);
                }
            }
        }
        else if(esta2[0] == ST_DESACTIVADO) // && primeraEjecucion != 0 && !acabado
        {
            //Code for saving the directions of the user to a png image
            if (takeScreenShot){
                PImage screenShot = get();
                screenShot.save("/storage/emulated/0/CarpetaPasos/userDirection" + fileNumber + ".png");
                takeScreenShot = false;
                fileNumber++;
            }
        }
        if (esta2[1] == ST_REINICIANDO){
            //Limpiamos las posiciones y rotaciones anteriores
            movingBackground.clearPosicionesAnteriores();
            movingBackground.clearRotacionesAnteriores();
            userPosition = new PVector(width/2.0f, height/2.0f);
            movingBackground.draw(userPosition);
            pasosGlobal = 0;
            // Cambiamos la orientación inicial
            reiniciarNorte();

            mqEst.run();
        }

        // Dibujamos al usuario en el centro de la pantalla
        paintPlayer();
    }


    private void paintPlayer(){
    /* Code for drawing the user position */
        pg.beginDraw();
        pg.stroke(0, 0, 0);

        // Here it's easier with this color mode
        pg.colorMode(HSB, 360, 100, 100);
        light = colorEffect(light);
        pg.fill(210, 100, light);
        pg.strokeWeight(4.0f);
        pg.pushMatrix();
        pg.translate(width/2.0f, height/2.0f);
        pg.rotate(rotacionActual);
        pg.ellipse(0, 0, width/8, width/8);
        // We change the color mode back
        pg.colorMode(RGB, 255, 255, 255);

        //Drawing the direcction arrows
        pg.noFill();
        pg.stroke(255);
        pg.strokeWeight(10.0f);
        pg.strokeJoin(ROUND);
        pg.beginShape();
        pg.vertex(- width/20, 0);
        pg.vertex(0, - width/20);
        pg.vertex(width/20, 0);
        pg.endShape();
        pg.popMatrix();
        pg.endDraw();
        image(pg, 0, 0);
        //Finished drawing the direction arrows
    }

    // Utilizamos la primera rotación recibida como nuestro Norte
    public void setNorte(float north){
        norte = north;
    }

    public float getNorte(){
        return norte;
    }

    // Para cambiar de orientación inicial
    private void reiniciarNorte(){
        norte = -10000.0f;
    }

    // Calculamos nuestra rotación real
    public void setRotacionActual(float rotacion){
        float tempRotacion = rotacion - norte;
        if (tempRotacion < 0){
            rotacionActual = TWO_PI + tempRotacion;
        }
        else{
            rotacionActual = rotacion - norte;
        }
    }

    public boolean guardarAceleracionesArray(){
        float[] array = new float[4];
        array[0] = ax;
        array[1] = ay;
        array[2] = az;
        float modulo = (array[0] * array[0]) + (array[1] * array[1]) + (array[2] * array[2]);
        array[3] = modulo;
        pasoActual.add(array);

        //Cogemos el tiempo del primer valor
        if(pasoActual.size() == 1)
            milisegundosPaso = millis();

        if (pasoActual.size() >= 16) {
            //Pasamos el paso actual y el tiempo que ha tardado;
            if(ejecutarComprobacionPaso(pasoActual, millis() - milisegundosPaso)){
                pasoActual = new ArrayList<float[]>();
                milisegundosPaso = 0.0f;
                setReiniciarMaxAceleracion(true);
                return true;
            }
            pasoActual = new ArrayList<float[]>();
            milisegundosPaso = 0.0f;
        }
        return false;
    }

    private boolean ejecutarComprobacionPaso(ArrayList<float[]> pasoComprobar, float tiempo){
        if (hayPaso(pasoComprobar, tiempo)) {
            movingBackground.setRotacionAnterior(rotacionActual);
            pasosGlobal++;

            float movimientoX = (longitudPaso*sin(rotacionActual));
            float movimientoY = (longitudPaso*cos(rotacionActual));
            Log.i(Positioning.LOG_TAG, "Longitud Paso: " + longitudPaso);
            //Changing position of user
            userPosition.x = userPosition.x + movimientoX;
            userPosition.y = userPosition.y - (movimientoY);
            movingBackground.setDistanciaPaso(movimientoX, movimientoY);
            movingBackground.setPosicionesYRotaciones();
            //Si pasan menos de dos segundos y medio entre dos pasos encontrados, significa que está andando y hay que contar dos pasos
            if (abs(millis() - tiempoSiguientePaso) < 2500) {
                pasosGlobal++;
                userPosition.x = userPosition.x + (movimientoX);
                userPosition.y = userPosition.y - (movimientoY);
                movingBackground.setPosicionesYRotaciones();
            }
            tiempoSiguientePaso = millis();
            return true;
        }
        return false;
    }

    //Recoge el conjunto de entrenamiento y guarda todos los datos en una matriz de matrices [ [ [x,y,z,mod], [x,y,z,mod] ... ], [ [x,y,z,mod], [x,y,z,mod] ... ] ... ]
    private ArrayList<ArrayList<float[]>> guardarDatosMatriz(){
        ArrayList<ArrayList<float[]>> matrizPrincipal = new ArrayList<ArrayList<float[]>>();
        for (int i = 0; i < tablaLectura.getRowCount(); i++){
            //Mi primera fila contiene una string, no quiero cogerla
            if(i != 0){
                ArrayList<float[]> matrizTemp = new ArrayList<float[]>();
                //Coger solo de 16 en 16 elementos
                boolean guardar = false;
                for(int j = i; j < i + 16 && j < tablaLectura.getRowCount(); j++){
                    TableRow  fila = tablaLectura.getRow(j);
                    if(Float.isNaN(fila.getFloat(0))){
                        i = i + j;
                        break;
                    }
                    if (j +1 == i + 16){
                        guardar = true;
                    }
                }
                if (guardar == true){
                    for(int z = i; z < i+16 && z < tablaLectura.getRowCount(); z++){
                        TableRow fila = tablaLectura.getRow(z);
                        float[] array = new float[4];
                        if(!Float.isNaN(fila.getFloat(0))){
                            array[0] = fila.getFloat(0);
                            array[1] = fila.getFloat(1);
                            array[2] = fila.getFloat(2);
                            array[3] = fila.getFloat(3);
                            matrizTemp.add(array);
                        }
                        if (z + 1 >= i+16){
                            i = z;
                            break;
                        }
                    }
                    matrizPrincipal.add(matrizTemp);
                }
            }
        }
        return matrizPrincipal;
    }

    // Devuelve true si hay paso
    private boolean hayPaso(ArrayList<float[]> pasoComprobar, float tiempo){
        if (calcularNumeroPicosModulo(pasoComprobar) && calcularDistanciaEuclidea(pasoComprobar)){
            //Calculamos la longitud del paso, lo relativizamos según el tamaño de pantalla y lo ponemos
            setLongitudPaso(calcularLongitudRelativa(calcularLongitudPaso(tiempo)));
            return true;
        }
        return false;
    }

    private float calcularLongitudRelativa(float longitudR) {
        //El 100 es porque son 100cm = 1 metro
        return (longitudR*movingBackground.getWidthTile())/100;
    }

    private float calcularLongitudPaso(float tiempo) {
        //Log.i(Positioning.LOG_TAG, "Longitud del Paso: " + (2*((tiempo/100)*(maxAceleracionY))));
        //El 2 es porque sale mejor el calculo así
        return 4*((tiempo/100)*(maxAceleracionY));
    }

    private boolean calcularDistanciaEuclidea(ArrayList<float[]> vector){
        float averageVec = 0.0f;
        int numDistanciasPequenyas = 0;
        for (int i = 0; i < matrizPasos.size(); i++){
            ArrayList<float[]> matrizTemp = matrizPasos.get(i);
            for(int j = 0; j < matrizTemp.size(); j++){
                float[] tmpArray = matrizTemp.get(j);
                float[] vec = vector.get(j);
                float distanciaVec = sqrt((tmpArray[0]-vec[0])*(tmpArray[0]-vec[0]) + (tmpArray[1]-vec[1])*(tmpArray[1]-vec[1]) + (tmpArray[2]-vec[2])*(tmpArray[2]-vec[2]));
                averageVec += distanciaVec;
            }
            averageVec = averageVec/matrizTemp.size();
            //Log.i(Positioning.LOG_TAG, "numDistanciasPequenyas: " + numDistanciasPequenyas);
            if (averageVec < 3.5f){
                numDistanciasPequenyas++;
                if (numDistanciasPequenyas > 3)
                    return true;
            }
            else{
                averageVec = 0.0f;
            }
        }

        return false;
    }

    private boolean calcularNumeroPicosModulo(ArrayList<float[]> vector){
        int numeroPicos = 0;
        for(int j = 0; j < vector.size(); j++){
            float[] vec = vector.get(j);
            //println(vec[3]);
            if(vec[3] >= 98.0f || vec[3] <= 74.0f){
                numeroPicos++;
            }
        }
        //println("Numero Picos: " + numeroPicos);
        if (numeroPicos >= 8){
            return true;
        }
        return false;
    }

    private int colorEffect(int light){
        // Color effect for the ellipse, it changes it's colour gradually
        if(frameCount - frames >= 2){
            frames = frameCount;
            light = light + colorChange;
            if (light >= 95)
            {
                colorChange = -colorChange;
            }
            else if (light <= 50){
                colorChange = -colorChange;
            }
        }
        return light;
    }

    public void setLongitudPaso(float longitudPaso) {
        this.longitudPaso = longitudPaso;
    }

    public void setMaxAceleracionY(float maxAceleracionY) {
        this.maxAceleracionY = maxAceleracionY;
    }

    //Nos dice cuando debemos borrar la máxima aceleración
    public void setReiniciarMaxAceleracion(boolean reiniciarMaxAceleracion) {
        this.reiniciarMaxAceleracion = reiniciarMaxAceleracion;
    }

    //Nos dice cuando debemos borrar la máxima aceleración
    public boolean getReiniciarMaxAceleracion() {
        return this.reiniciarMaxAceleracion;
    }

    public void mousePressed(){
        mqEst.transite();
    }

    public void onResume() {
        super.onResume();
        if (manager != null) {
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void onPause() {
        super.onPause();
        if (manager != null) {
            manager.unregisterListener(listener);
        }
    }

    class AccelerometerListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            acelerometroListo = true;
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
    class Boton {
        PVector location;
        PImage imagen, bocaArriba;
        float alto, ancho;
        String texto;
        boolean botonApretado = false, hayImagen = false;
        int colorTexto = 0, anchoPincel = 2, colorPincel = 0, idCarta = -1;
        public int primer_est = 0, actual_est = 0, num_est = 4;
        int colorBoton = color(150, 150, 150);
        int estado = -1;


        Boton(float x, float y, float anch, float alt, String text){
            location = new PVector(x,y);
            alto = alt;
            ancho = anch;
            texto = text;
        }

        Boton(float x, float y, float anch, float alt, String text, int colorBot){
            location = new PVector(x,y);
            alto = alt;
            ancho = anch;
            texto = text;
            colorBoton = colorBot;
        }

        public void draw(){

            stroke(colorPincel);
            strokeWeight(anchoPincel);
            rectMode(CENTER);

            fill(colorBoton);
            rect(location.x, location.y, ancho, alto);

            fill(colorTexto);
            textSize(alto/3);
            textAlign(CENTER, CENTER);
            text(texto, location.x, location.y );

            if(hayImagen){
                imageMode(CENTER);
                image(imagen, location.x, location.y, ancho, alto);
            }

        }


        public void CambiarColor(int colorNuevo){
            colorBoton += colorNuevo;
        }

        public void CambiarImagen(PImage tmpImagen){
            this.imagen = tmpImagen;
        }

        public PVector getLocation(){
            return this.location;
        }

        public void setTexto(String _texto){
            this.texto = _texto;
        }

        public float getAlto(){
            return this.alto;
        }

        public float getAncho(){
            return this.ancho;
        }


        public boolean EstaMouseDentro(){
            boolean dentro = false;
            if (mouseX >= this.location.x - this.ancho/2.0f && mouseX <= this.location.x + this.ancho/2.0f){
                if (mouseY >= this.location.y - this.alto/2.0f && mouseY <= this.location.y + this.alto/2.0f){
                    dentro = true;
                }
            }
            return dentro;
        }

        public int cambiarEstado(){
            actual_est += 1;
            primeraEjecucion = 1;
            if (actual_est >= num_est){
                actual_est = primer_est;
            }
            return actual_est;
        }
    }
    class Estado {
        int ST_DESACTIVADO = 0, ST_TRABAJANDO = 1, ST_NO_REINICIO = 2, ST_REINICIANDO = 3;
        int id;
        ArrayList<PImage> imagenes;
        ArrayList<Boton> botones;

        Estado(int _id){
            id = _id;
            imagenes = new ArrayList<PImage>();
            botones = new ArrayList<Boton>();
        }

        public void run(){
            PImage tmpImg = new PImage();

            if ( id == ST_DESACTIVADO){
                for(int i = 0; i < botones.size(); i++){
                    botones.get(i).setTexto("Calcular Pasos");
                    botones.get(i).draw();
                }
            }

            else if (id == ST_TRABAJANDO){
                for(int i = 0; i < botones.size(); i++){
                    Boton botonTmp = botones.get(i);
                    botonTmp.setTexto("Pasos: " + pasosGlobal);
                    botonTmp.draw();
                }
            }
            else if (id == ST_NO_REINICIO){
                for(int i = 0; i < botones.size(); i++){
                    Boton botonTmp = botones.get(i);
                    botonTmp.setTexto("Reiniciar");
                    botonTmp.draw();
                }
            }
            else if (id == ST_REINICIANDO){
                for(int i = 0; i < botones.size(); i++){
                    Boton botonTmp = botones.get(i);
                    botonTmp.setTexto("Reiniciando...");
                    botonTmp.draw();
                }
            }
        }

        public int transite(){
            for(int i = 0; i < botones.size(); i++){
                if(botones.get(i).EstaMouseDentro()){
                    //Devuelvo el id del botón que ha sido apretado
                    return i;
                }
            }
            return -1;
        }

    }
    class MaquinaEstados{
        int estado;
        ArrayList<Estado> estados;
        boolean finPartida = false;
        int ST_DESACTIVADO = 0, ST_TRABAJANDO = 1, ST_NO_REINICIO = 2, ST_REINICIANDO = 3;

        MaquinaEstados(int _e){
            estado = _e;
            estados = new ArrayList<Estado>();

        }

        public void addState(int _id){
            estados.add(new Estado(_id));
        }

        public void img2State(String img, int st){
            PImage i = loadImage(img);
            estados.get(st).imagenes.add(i);
        }

        public void btn2State(Boton btn, int st){
            estados.get(st).botones.add(btn);
        }

        public void run(){
            for(int i = 0; i < estados.size(); i++)
                estados.get(i).run();
        }

        public void transite(int _e){
            estado = _e;
        }

        public void transite(){
            //Comprobamos en qué estado se ha producido el mousePressed()
            for(int i = 0; i < estados.size(); i++){
                Estado estadoTmp = estados.get(i);
                int idBotonApretado = estadoTmp.transite();
                if (idBotonApretado != -1){
                    Boton botonTmp = estadoTmp.botones.get(idBotonApretado);
                    int new_st = botonTmp.cambiarEstado();
                    //Anyadimos al estado siguiente el boton que ha sido pulsado y lo eliminamos del anterior.

                    if(estadoTmp.id == ST_TRABAJANDO){
                        botonTmp.CambiarColor(-40);
                        if(new_st > 1)
                            new_st = 0;
                        estados.get(new_st).botones.add(botonTmp);
                        estadoTmp.botones.remove(idBotonApretado);
                        break;
                    }
                    else if(estadoTmp.id == ST_DESACTIVADO){
                        botonTmp.CambiarColor(40);
                        if(new_st > 1)
                            new_st = 1;
                        estados.get(new_st).botones.add(botonTmp);
                        estadoTmp.botones.remove(idBotonApretado);
                        break;
                    }
                    else if(estadoTmp.id == ST_NO_REINICIO){
                        botonTmp.CambiarColor(40);
                        if(new_st < 2)
                            new_st = 3;
                        estados.get(new_st).botones.add(botonTmp);
                        estadoTmp.botones.remove(idBotonApretado);
                        break;
                    }
                    else if(estadoTmp.id == ST_REINICIANDO){
                        botonTmp.CambiarColor(-40);
                        if(new_st < 2)
                            new_st = 2;
                        estados.get(new_st).botones.add(botonTmp);
                        estadoTmp.botones.remove(idBotonApretado);
                        break;
                    }
                }

            }
        }

        public int numEstados(){
            return this.estados.size();
        }

        public int[] EstadoDeBoton(){
            int[] est = new int[2];
            int control = 0;
            for(int i = 0; i < estados.size(); i++){
                if(estados.get(i).botones.size() > 0){
                    est[control] = i;
                    control++;
                }
            }
            return est;
        }



    }
    class MovingBackground{
        PImage backgroundImage;
        PVector positionsBack, longitudDesplazamiento, lastPosition;
        float aspectRatio = 0.0f, rotacionAnterior = 0.0f;
        int xScroll = 0, yScroll = 0, vecesTextura = 1;
        PGraphics pgBack;
        boolean repetirTextura = true;
        private ArrayList<PVector> posicionesAnteriores;
        private ArrayList<Float> rotacionesAnteriores;

        int widthTile, heightTile;

        MovingBackground(){
            backgroundImage = loadImage("texture6.jpg");
            aspectRatio = backgroundImage.width/backgroundImage.height;
            pgBack = createGraphics(width, height);
            longitudDesplazamiento = new PVector(0.0f, 0.0f);
            widthTile = width/6;
            heightTile = width/6;

            posicionesAnteriores = new ArrayList<PVector>();
            rotacionesAnteriores = new ArrayList<Float>();
        }

        public void setup(){
        }

        public void draw(PVector userPosition){
            // For creating an infinite scrolling
            xScroll = ((int)userPosition.x % widthTile);
            yScroll = ((int)userPosition.y % heightTile);

            // The image must be bigger than the screen size
            image(backgroundImage, xScroll - widthTile, width + yScroll - heightTile/2, (width + 2*widthTile), (width*aspectRatio) + 2*heightTile);
            image(backgroundImage, xScroll - widthTile, yScroll - heightTile, (width + 2*widthTile), (width*aspectRatio) + 2*heightTile );

            paintLastPosition(userPosition);
        }

        public void paintLastPosition(PVector userPosition){

            positionsBack = new PVector();
            // The last known position of the user
            positionsBack.x = width - userPosition.x;
            positionsBack.y = height - userPosition.y;

            pgBack = createGraphics(width, height);

            pgBack.beginDraw();
            //Pintamos todas las posiciones anteriores
            for(int i = 0; i < posicionesAnteriores.size(); i++)
            {

                posicionesAnteriores.get(i).x = posicionesAnteriores.get(i).x + longitudDesplazamiento.x;
                posicionesAnteriores.get(i).y = posicionesAnteriores.get(i).y + longitudDesplazamiento.y;

                /* Code for drawing the user position */
                pgBack.stroke(0, 0, 0);

                // Here it's easier with this color mode
                pgBack.colorMode(HSB, 360, 100, 100);
                pgBack.fill(0, 0, 50);
                pgBack.strokeWeight(5.0f);

                pgBack.pushMatrix();
                pgBack.translate(posicionesAnteriores.get(i).x, posicionesAnteriores.get(i).y);
                pgBack.rotate(rotacionesAnteriores.get(i));
                pgBack.ellipse(0, 0, width/8, width/8);
                // We change the color mode back
                pgBack.colorMode(RGB, 255, 255, 255);

                //Drawing the direcction arrows
                pgBack.noFill();
                pgBack.stroke(255);
                pgBack.strokeWeight(10.0f);
                pgBack.strokeJoin(ROUND);
                pgBack.beginShape();
                pgBack.vertex(- width/20, 0);
                pgBack.vertex(0, - width/20);
                pgBack.vertex(width/20, 0);
                pgBack.endShape();
                pgBack.popMatrix();
            }
            pgBack.endDraw();
            image(pgBack, 0, 0);
            //Finished drawing the direction arrows


        }

        public void setRotacionAnterior(float rotationNew){
            rotacionAnterior = rotationNew;
        }

        //Usado para determinar hacia donde va el usuario
        public void setDistanciaPaso(float v, float v1) {
            longitudDesplazamiento.x = -v;
            longitudDesplazamiento.y = v1;
        }

        //Ponemos la nueva posición en el array
        public void setPosicionesYRotaciones(){
            // We save the last user positions and rotation into this array
            posicionesAnteriores.add(new PVector(width/2.0f, height/2.0f));
            rotacionesAnteriores.add(rotacionAnterior);
        }

        public void clearPosicionesAnteriores(){
            posicionesAnteriores = new ArrayList<PVector>();
        }

        public void clearRotacionesAnteriores(){
            rotacionesAnteriores = new ArrayList<Float>();
        }

        public float getWidthTile(){
            return widthTile;
        }
    }
    public void settings() {  fullScreen(); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#F01B1B", "GUI" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    @Override
    public void onPathChanged(PathData pPathData) {
        // convert pathdata to userposition
        //Log.i(Positioning.LOG_TAG, "Path changed! time(ms): " + millis() + " angle: " + pPathData.angle + " way points: " + pPathData.positions.size() );
    }
}
