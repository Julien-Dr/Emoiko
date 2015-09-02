package processing.test.emoiko;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ketai.cv.facedetector.*; 
import android.content.Intent; 
import android.content.Context; 
import android.os.Environment; 
import android.net.Uri; 
import java.io.File; 
import apwidgets.*; 
import ketai.ui.*; 
import android.view.MotionEvent; 
import ketai.ui.*; 
import controlP5.*; 
import android.app.Activity; 
import android.os.Bundle; 
import android.widget.ImageView; 
import java.io.InputStream; 
import android.net.Uri; 
import android.graphics.Bitmap; 
import android.graphics.BitmapFactory; 
import android.provider.MediaStore; 
import android.database.Cursor; 
import android.content.Intent; 
import apwidgets.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class EmoIko extends PApplet {













int MAX_FACES = 10;
KetaiSimpleFace[] faces = new KetaiSimpleFace[MAX_FACES];

APMediaPlayer player ;

PImage iconSplash, iconCam, iconPhotos, iconShot, iconCheck, iconRotateR, iconRotate, iconShare, testImg, inconNoFace;

StringBuilder pathToSavedImg;
String stringtmp;

boolean isRecognized, isEmoiko, isKeyboardOpen, isShared;

PFont font, fontField;

float     Xvisage, Yvisage, largeurVisage, hauteurVisage, XoeilG, YoeilG, XoeilD, 
YoeilD, tailleOeil, Xnez, Ynez, largeurNez, hauteurNez, Xbouche, Ybouche, 
largeurBouche, hauteurBouche, Xmenton, Ymenton, largeurMenton, hauteurMenton, XjoueG, YjoueG, largeurJoueG, hauteurJoueG, XjoueD, 
YjoueD, largeurJoueD, hauteurJoueD, Xfront, 
Yfront, largeurFront, hauteurFront;

//save
Uri uri;
File file;

//Button buttonPhotos, buttonCam, buttonCapture;
Button buttonCam, buttonRotate, buttonRotateR, buttonCheck, buttonShare;
Textfield textFieldLong;

ButtonOpen buttonOpen;
//Loader Loader;
//ButtonCheck buttonCheck;
PGraphics big;
public void setup() {
 
  orientation(PORTRAIT);
  //cam = new KetaiCamera(this, 480, 640, 15);
  cp5 = new ControlP5(this);

  rectMode(CENTER);

  font = createFont("Bello.ttf", 128);
  fontField = loadFont("Bello-Script-48.vlw");
  textFont(font, 32);

  //pour stocker le chemin
  pathToSavedImg = new StringBuilder();

  player = new APMediaPlayer ( this );
  player . setMediaFile ( "snapshot.mp3" );

  background(255);

  iconSplash=loadImage("icon-splash.png");
  iconCam=loadImage("icon-cam.png");
  iconPhotos=loadImage("icon-photos.png");
  iconShot=loadImage("icon-cam_125-over.png");
  inconNoFace=loadImage("noFace.png");

  buttonOpen = new ButtonOpen((displayWidth*0.5f), displayHeight-85, 125);

  buttonRotate = cp5.addButton("buttonRotate")
    .setPosition(displayWidth/2-135, displayHeight-150)
      .setImages(loadImage("icon-rotateclock_125.png"), loadImage("icon-rotateclock_125-over.png"), loadImage("icon-rotateclock_125.png")).updateSize();
  buttonRotateR = cp5.addButton("buttonRotateR")
    .setPosition(displayWidth/2+135, displayHeight-150)
      .setImages(loadImage("icon-rotaterclock_125.png"), loadImage("icon-rotaterclock_125-over.png"), loadImage("icon-rotaterclock_125.png")).updateSize();
  buttonCheck = cp5.addButton("buttonCheck")
    .setPosition(displayWidth-135, displayHeight-150)
      .setImages(loadImage("icon-check_125.png"), loadImage("icon-check_125-over.png"), loadImage("icon-check_125.png")).updateSize();
  buttonShare = cp5.addButton("buttonShare")
    .setPosition(displayWidth-135, displayHeight-150)
      .setImages(loadImage("icon-share_125.png"), loadImage("icon-share_125.png"), loadImage("icon-share_125.png")).updateSize();

  textFieldLong = cp5.addTextfield("textFieldLong")
    .setPosition(0, 0)
      .setSize(displayWidth, 100)
      .setFont(fontField)
          .setFont(50)
            .setFocus(true)
              .setColor(color(255, 0, 0))
                .setText("Your text here + Return")
                  .setColorBackground(color(255))
                    .setColorForeground(color(255, 0, 0))
                      .setColorActive(color(255, 0, 0))
                        .setLabelVisible(false)
                          .setAutoClear(false)
                            .setCaptionLabel(" ")  ;
 
  textFieldLong.hide();
  buttonRotate.hide();
  buttonRotateR.hide();
  buttonCheck.hide();
  buttonShare.hide();
  //knobEffects.hide();

  orientation=0;
  isOpen = false;
  isChecked = false;
  isCamera = false;
  isKeyboardOpen = false;
  isEmoiko = false;

  gesture = new KetaiGesture(this);


  imageMode(CENTER);
  image(iconSplash, displayWidth/2, displayHeight/2, displayWidth/3, displayWidth/3 );

  mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
  mediaChooser.setType("image/*");
}

/*
void keyPressed() {
 // doing other things here, and then:
 if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
 keyCode = 0;  // don't quit by default
 Camera();
 }
 }
 */
public void draw() {
  //Camera();
  Open();


  if (isChecked == true) {
    saveImgTemp();
    background(255);
    isChecked =false;
  }

 if (isImgSaved==true) { 

    isImgSaved=false;
    // println(stringtmp);

    if (stringtmp != null && isRecognized == false) {

      buttonOpen.hide();
      // buttonCam.hide();
      pushMatrix();
      testImg = loadImage(stringtmp+stringDate);

      filter(GRAY);
      if (testImg != null) {

        faces = KetaiFaceDetector.findFaces(testImg, MAX_FACES);
      }
      // image(testImg, 0, 0);


      if (faces.length > 0) {
        isRecognized=true;
        print("Face   :  " + stringtmp);
        for (int i=0; i < faces.length; i++)
        {
          pushMatrix();
          noFill();
          Xvisage =faces[i].location.x;
          Yvisage = faces[i].location.y;
          largeurVisage = 2.5f*faces[i].distance;
          hauteurVisage = 3*faces[i].distance;
          XoeilG = faces[i].location.x-faces[i].distance/2;
          YoeilG = faces[i].location.y;
          XoeilD = faces[i].location.x+faces[i].distance/2;
          YoeilD = faces[i].location.y;
          tailleOeil = faces[i].distance/1.5f;
          Xnez =  faces[i].location.x;
          Ynez =  faces[i].location.y+0.5f*faces[i].distance;
          largeurNez =  faces[i].distance;
          hauteurNez = 0.5f*faces[i].distance;
          Xbouche = faces[i].location.x;
          Ybouche = faces[i].location.y+faces[i].distance;
          largeurBouche = faces[i].distance ;
          hauteurBouche = 0.5f*faces[i].distance;
          XjoueG = faces[i].location.x-faces[i].distance/1.1f;
          YjoueG = faces[i].location.y+faces[i].distance/1.2f;
          largeurJoueG = -faces[i].distance/1.3f;
          hauteurJoueG = faces[i].distance/.9f;
          largeurMenton = faces[i].distance ;
          hauteurMenton = 0.5f*faces[i].distance;
          Xmenton = faces[i].location.x;
          Ymenton = faces[i].location.y+faces[i].distance*1.5f;

          XjoueD = faces[i].location.x+faces[i].distance/1.1f;
          YjoueD = faces[i].location.y+faces[i].distance/1.2f;
          largeurJoueD = faces[i].distance/1.3f;
          hauteurJoueD = faces[i].distance/.9f; 
          Xfront =  faces[i].location.x;
          Yfront = faces[i].location.y-faces[i].distance;
          largeurFront =  2.5f*faces[i].distance;
          hauteurFront = faces[i].distance;
          popMatrix();
        }
        popMatrix();
      }
      else {
        isImgSaved=false;
        bimage = null;
        portrait = null;
        background(255);
        image(inconNoFace, displayWidth/2, displayHeight/2, displayWidth/3, displayWidth/3 );

        pushMatrix();
        fill(255, 0, 0);
        textSize(48);
        textAlign(CENTER, CENTER);
        text("Oups ! \nNo face in the picture\u2026\nTry again !", displayWidth/2, displayHeight/2+displayWidth/3+10);
        popMatrix();
        buttonOpen.show();
      }
    }
  }
  if (isRecognized == true) {
    pushMatrix();

    image(testImg, displayWidth/2, displayHeight/2, displayWidth, (displayWidth*testImg.height)/testImg.width);
    filter(GRAY);

    popMatrix();
    if (things.size() > 0)
      for (int i = things.size()-1; i >= 0; i--)
      {
        Thing t = things.get(i);
        if (t.isDead()) {
          things.remove(t);
        }
        else
        {
          t.draw();
        }
      }
  }
  if (ikoText !=null) {
    pushMatrix();
    fill(255, 0, 0);
    float tailletexte;
    tailletexte = largeurVisage/3;
    textSize(tailletexte);
    text("*", ikoTextX-tailletexte*0.35f, ikoTextY+tailletexte*0.4f);
    textSize(50);
    text("* " + ikoText.toLowerCase(), 20, height-50);
    fill(0, 255, 0);
    strokeWeight(10);
    //    rect(XjoueG, YjoueG, largeurJoueG, hauteurJoueG);
    popMatrix();
    isEmoiko = true;
  }

  if (isEmoiko == true) {
    buttonShare.show();
    isEmoiko = false;
  }

  if (textFieldLong.isMousePressed()==true ) {
    textFieldLong.setText("");
  }

  if (isShared == true) {
    buttonOpen.update();
    buttonOpen.show();
  }
} // void draw()

public void textFieldLong(String theText) {
  // receiving text from controller textinput
  println("a textfield event for controller 'textinput': "+theText);
}

String letters = "";

public void keyPressed() {
//  if (key = "\u00d4\u00f8\u00f8") {
//  print("well");
//  }
  if (key == BACKSPACE)
  {
    if (textFieldLong.getText().length() > 0)
    {
      letters = letters.substring(0, letters.length()-1);
    }
  } 
  else if (textWidth(letters+key) < width)
  {
    letters = letters+key;
    textFieldLong.setText(letters);
  }
  if (key == ENTER || key == RETURN) {
    ikoText = textFieldLong.getText();
    textFieldLong.hide();
    KetaiKeyboard.toggle(this);
  }
  
  print(key);
}

String ikoText;
float ikoTextX, ikoTextY;
int colorCircle, epaisseur;
//-------------------------------------------------------------------------- Gestures


KetaiGesture gesture;
float Size = 10;
float Angle = 0;
ArrayList<Thing> things = new ArrayList<Thing>();

class Thing
{
  PVector location;
  PVector plocation;
  String  mText="";
  float life = frameRate * 3;

  public Thing(String _text, float x, float y)
  {
    mText = _text;
    location = new PVector(x, y);
  }

  public Thing(String _text, float x, float y, float px, float py)
  {
    mText = _text;
    location = new PVector(x, y);
    plocation = new PVector(px, py);
  }

  public void draw()
  {

    if ( location.x > Xvisage - largeurVisage/2 && location.x < Xvisage + largeurVisage/2 && location.y > Yvisage - hauteurVisage/2 && location.y < Yvisage + hauteurVisage) {
      colorCircle = color(255, 0, 0, 255);
      strokeWeight(10);
      stroke(colorCircle);
    }
    else {
      colorCircle = color(0, 0, 0, 0);
      noStroke();
    }
    pushStyle();
    //stroke(255, 0, 0, map(life, frameRate*3, 0, 255, 0));
    fill(255, 0, 0, map(life, frameRate*3, 0, 255, 0));
    pushMatrix();
    noFill();
    ellipse(location.x, location.y, map(life, frameRate*3, 0, 400, 0), map(life, frameRate*3, 0, 400, 0));
    popMatrix();

    ikoText = mText;
    ikoTextX = location.x;
    ikoTextY = location.y;
    life=life-10;
    if (life > 0)
     // text(mText, location.x, location.y);

    if (plocation != null)
      line(location.x, location.y, plocation.x, plocation.y);
    popStyle();
    pushStyle();


    popStyle();
  }

  public boolean isDead()
  {
    return(life <= 0);
  }
}
public void onDoubleTap(float x, float y)
{
  textFieldLong.hide();
  if ( x > XoeilG-tailleOeil/2 && x<XoeilG+tailleOeil/2 && y>YoeilG-tailleOeil/2 && y<YoeilG+tailleOeil/2) {
    things.add(new Thing("An eye for an eye !", x, y));
  }
  if ( x > XoeilD-tailleOeil/2 && x<XoeilD+tailleOeil/2 && y>YoeilD-tailleOeil/2 && y<YoeilD+tailleOeil/2) {
    things.add(new Thing("My eye!  !", x, y));
  }
  if ( x > Xbouche-largeurBouche/2 && x<Xbouche+largeurBouche/2 && y>Ybouche-hauteurBouche/2 && y<Ybouche+hauteurBouche/2) {
    things.add(new Thing("Shut up !", x, y));
  }
  if ( x > Xnez-largeurNez/2 && x<Xnez+largeurNez/2 && y>Ynez-hauteurNez/2 && y<Ynez+hauteurNez/2) {
    things.add(new Thing("Keep your nose out of my business !", x, y));
  }
  if ( x > XjoueG-largeurJoueG/2 && x<XjoueG+largeurJoueG/2 && y>YjoueG-hauteurJoueG/2 && y<YjoueG+hauteurJoueG/2) {
    things.add(new Thing("Slap !", x, y));
    println("j gauche");
  }     
    if ( x > XjoueD-largeurJoueD/2 && x<XjoueD+largeurJoueD/2 && y>YjoueD-hauteurJoueD/2 && y<YjoueD+hauteurJoueD/2) {
    things.add(new Thing("Slap !", x, y));
  }     
     if ( x > Xmenton-largeurMenton/2 && x<Xmenton+largeurMenton/2 && y>Ymenton-hauteurMenton/2 && y<Ymenton+hauteurMenton/2) {
    things.add(new Thing("Menton !", x, y));
  }   
       if ( x > Xfront-largeurFront/2 && x<Xfront+largeurFront/2 && y>Yfront-hauteurFront/2 && y<Yfront+hauteurFront/2) {
    things.add(new Thing("Front !", x, y));
  }   
  else {
    // things.add(new Thing( "single", x, y));
  }
}

public void onTap(float x, float y)
{
  textFieldLong.hide();
  if ( x > XoeilG-tailleOeil/2 && x<XoeilG+tailleOeil/2 && y>YoeilG-tailleOeil/2 && y<YoeilG+tailleOeil/2) {
    things.add(new Thing("Blink !", x, y));
  }
  if ( x > XoeilD-tailleOeil/2 && x<XoeilD+tailleOeil/2 && y>YoeilD-tailleOeil/2 && y<YoeilD+tailleOeil/2) {
    things.add(new Thing("Blink !", x, y));
  }
  if ( x > Xbouche-largeurBouche/2 && x<Xbouche+largeurBouche/2 && y>Ybouche-hauteurBouche/2 && y<Ybouche+hauteurBouche/2) {
    things.add(new Thing("Kiss !", x, y));
  }
  if ( x > Xnez-largeurNez/2 && x<Xnez+largeurNez/2 && y>Ynez-hauteurNez/2 && y<Ynez+hauteurNez/2) {
    things.add(new Thing("You're so funny !", x, y));
  }
  if ( x > XjoueG-largeurJoueG/2 && x<XjoueG+largeurJoueG/2 && y>YjoueG-hauteurJoueG/2 && y<YjoueG+hauteurJoueG/2) {
    things.add(new Thing("Caress !", x, y));
  }     
    if ( x > XjoueD-largeurJoueD/2 && x<XjoueD+largeurJoueD/2 && y>YjoueD-hauteurJoueD/2 && y<YjoueD+hauteurJoueD/2) {
    things.add(new Thing("Caress !", x, y));
  }     
     if ( x > Xmenton-largeurMenton/2 && x<Xmenton+largeurMenton/2 && y>Ymenton-hauteurMenton/2 && y<Ymenton+hauteurMenton/2) {
    things.add(new Thing("Chin up !", x, y));
  }   
       if ( x > Xfront-largeurFront/2 && x<Xfront+largeurFront/2 && y>Yfront-hauteurFront/2 && y<Yfront+hauteurFront/2) {
    things.add(new Thing("I'm thinking of you !", x, y));
  }   
  else {
    // things.add(new Thing( "single", x, y));
  }

}

public void onLongPress(float x, float y)
{
  letters = " ";
  textFieldLong.show();
   KetaiKeyboard.toggle(this);
  
  things.add(new Thing("LONG", x, y));
 // println("long");
}

//the coordinates of the start of the gesture, 
//     end of gesture and velocity in pixels/sec
public void onFlick( float x, float y, float px, float py, float v)
{
   textFieldLong.hide();
  things.add(new Thing("FLICK:"+v, x, y, px, py));
  println("flick");
}

public void onPinch(float x, float y, float d)
{
  textFieldLong.hide();
  //Size = constrain(Size+d, 10, 2000);
  println("pinch");
}

public boolean surfaceTouchEvent(MotionEvent event) {

  //call to keep mouseX, mouseY, etc updated
  super.surfaceTouchEvent(event);

  //forward event to class for processing
  return gesture.surfaceTouchEvent(event);
}

ControlP5 cp5;
int orientation;
boolean isChecked, isCamera;


public void controlEvent(ControlEvent theEvent) {
  // println(theEvent.getController().getName());
  if (theEvent.getController().getName()=="buttonRotate") {
    orientation++;
  }
  if (theEvent.getController().getName()=="buttonRotateR") {
    orientation--;
  }
  if (theEvent.getController().getName()=="buttonCheck") {
    if (isChecked == true) {
      isChecked=false;
    }
    else {
      isOpen=false;
      //  portrait=null;    
      buttonRotate.hide();
      buttonRotateR.hide();
      buttonCheck.hide();
      isChecked=true;
    }
  }
  if (theEvent.getController().getName()=="buttonShare") {
    buttonShare.hide();
    saveImgTemp();
    share(uri);
  }
}

//-------------------------------------------------------------------------- Button Open Image











public String pathToLocalImg;
Intent mediaChooser;
PImage localImg;
Bitmap bimage;
PImage  portrait ;

class ButtonOpen {
  float x, y; // The x- and y-coordinates
  float taille; // Dimension (width and height)
  ButtonOpen(float xp, float yp, float s) {
    x = xp;
    y = yp;
    taille = s;
  }
  // Updates the over field every frame
  public void update() {  
    if ((mouseX >= x-taille/2) && (mouseX <= x + taille/2) &&
      (mouseY >= y-taille/2) && (mouseY <= y + taille/2) && mousePressed == true ) {
      buttonOpen.hide();
      startActivityForResult(mediaChooser, 1);
  
    }
    else {
    }
    image( iconPhotos, x, y, taille, taille);
  }
  public void hide() {
    x=0;
    y=0;
    taille=0;
  }
  public void show() {

    x=displayWidth-(73);
    y=displayHeight-(240);
    taille = 125;
  }
}
@Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
  try {    
    isOpen = false;
    // FilePath = data.getData().toString();
        if (isShared == true) {
          //stringtmp=null;
          ikoText=null;
          background(255);
          isShared=false;
        bimage=null;
        portrait=null;
        isImgSaved=false;
        buttonOpen.hide();
        textFieldLong.hide();
        buttonRotate.hide();
        buttonRotateR.hide();
        buttonCheck.hide();
        buttonShare.hide();
        orientation=0;
        isOpen = false;
        isChecked = false;
        isCamera = false;
        isKeyboardOpen = false;
        isEmoiko = false;
        isRecognized=false;
      }
    Uri selectedImage = data.getData();
    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
    bimage = BitmapFactory.decodeStream(imageStream);
    bimage = Bitmap.createScaledBitmap(bimage, bimage.getWidth(), bimage.getHeight(), true);
    background(255);
    portrait = new PImage(bimage);
    isOpen = true;
  }
  catch (IOException e) {
    println("help!");
  }
}

//-------------------------------------------------------------------------- Open + Rotate
boolean isOpen;
//PGraphics big;
PImage newImage;
public void Open() {
  buttonOpen.update();
  if (isOpen == true) {
    //  
    if (portrait!=null) {
      //  buttonOpen.hide();
      //  buttonCam.hide();
      background(255);
      pushMatrix();
      translate(displayWidth/2, displayHeight/2);
      rotate( orientation*HALF_PI);
      image(portrait, 0, 0, displayWidth, (displayWidth*portrait.height)/portrait.width);
      popMatrix();

      buttonRotate.setPosition(displayWidth/2-135, displayHeight-150).show();
      buttonRotateR.setPosition(displayWidth/2+10, displayHeight-150).show();
      buttonCheck.setPosition(displayWidth-135, displayHeight-150).show();
    }
  }
}
boolean isImgSaved;
String stringDate;
//-------------------------------------------------------------------------- Save
public void saveImgTemp() {
  isImgSaved=false;  
  bimage = null;
  portrait = null;
  if (pathToSavedImg.toString()=="") {
    //Le chemin ou sera enregistr\u00e9 l'image
    pathToSavedImg.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "EmoIko" + File.separator +"EmoIko"+"_");
    stringtmp = pathToSavedImg.toString();
    stringDate = year()+"-"+month()+"-"+day()+"_"+hour()+"-"+minute()+"-"+second()+".jpg";
    file = new File(stringtmp+stringDate);
    file.getParentFile().mkdirs();
    save(stringtmp+stringDate);
     background(255);
  }
  else {
    stringDate = year()+"-"+month()+"-"+day()+"_"+hour()+"-"+minute()+"-"+second()+".jpg";
    file = new File(stringtmp+stringDate);
    file.getParentFile().mkdirs();
    //on enregistre l'image
    save(stringtmp+stringDate);
     background(255);
  }
  //get uri from file
  uri = Uri.fromFile(file); 
  //refresh gallery to include the new file
  sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
  isImgSaved=true;
}
//-------------------------------------------------------------------------- Share
public void share(Uri uri) {

  Intent shareIntent = new Intent();
  shareIntent.setAction(Intent.ACTION_SEND);
  shareIntent.putExtra(Intent.EXTRA_TEXT   , "Made with EmoIko");
  shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
  shareIntent.setType("image/jpg");
  startActivity(Intent.createChooser(shareIntent, "Share your EmoIko"));
  isShared= true;
}
//-------------------------------------------------------------------------- Sound
public void onDestroy () {
  super . onDestroy ();
  if ( player != null ) {
    player . release ();
  }
} 

  public int sketchWidth() { return displayWidth; }
  public int sketchHeight() { return displayHeight; }
}
