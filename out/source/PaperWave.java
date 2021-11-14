import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PaperWave extends PApplet {

ArrayList<Wave> waves = new ArrayList<Wave>();
int total_waves = 50;
PVector maintarget;
public void setup() {
    
    background(0);
    float length = height*(2.0f/3.0f);
    for (int i = total_waves; i >= 0; i--) {
        float y = height - ((length * PApplet.parseFloat(i))/ (total_waves+1)); 
        Wave wave = new Wave(width/2, y);
        waves.add(wave);
    }
    maintarget = new PVector(width/2, height + 40);
    // Wave wave = new Wave(width/2, height/2);
    // waves.add(wave);
}
boolean a = false;
boolean b = false;
public void draw() {
    background(0);
    for (Wave wave : waves){
        wave.in_range(maintarget);
        wave.move();
        wave.display();
    }
    Wave wave0 = waves.get(0);

    if (maintarget.y < 0) {
        maintarget.y = height + 40;
    }
    maintarget.y -= 2;


}

class Wave {
    ArrayList<ShimmerPt> pts = new ArrayList<ShimmerPt>();
    float range = 100;
    PVector loc;
    int segments = 30;
    int num_raised = 15;
    public Wave(float x, float y){
        loc = new PVector(x, y);
        createpts();
    }   

    public void createpts(){
        for (int i = 0; i <= segments; i++){
            float x = (width*i/(segments - 2) - width/(segments - 2)) + loc.x - width/2;
            float y = loc.y;
            float raise = 0;
            
            float diff1 = abs(0 - x)/(width/2);
            float diff2 = abs(width - x)/(width/2);
            ShimmerPt pt;
            float mult = 30 * ((total_waves-waves.size())/5);
            if (diff1 < diff2){
                
                pt = new ShimmerPt(new PVector(x, y), diff1*mult);
            } else {
                pt = new ShimmerPt(new PVector(x, y), diff2*mult);
            }
            
            pts.add(pt);
        }
    }

    public void in_range(PVector l){
        boolean activate = false;
        for (ShimmerPt pt : pts){
            float dist = PVector.dist(pt.wavy, l);
            if (dist < range) {
                activate = true;
                break;
            } 
        }
        if (activate){
            for (ShimmerPt pt : pts){
                pt.activate();
            }
        } else {
            for (ShimmerPt pt : pts){
                pt.deactivate();
            }
        }
        
    }

    public void display(){
        fill(0);
        strokeWeight(1.5f);
        stroke(255);
        beginShape();
        curveVertex(-10, height*(5/4));
        curveVertex(-10, height*(5/4));
        for (ShimmerPt pt : pts) {
            curveVertex(pt.wavy.x, pt.wavy.y);
        }
        curveVertex(width + 10, height*(5/4));
        curveVertex(width + 10, height*(5/4));
        endShape(CLOSE);
    }

    float amp = 10;
    float speed = 2;
    public void move(){
        for (ShimmerPt pt : pts) {
            pt.shimmer();
        }
    }
}

class ShimmerPt {
    PVector start;
    PVector end;
    PVector target;
    PVector moving;
    PVector wavy;
    float amp = PApplet.parseInt(random(5, 10));
    float secondaryamp = PApplet.parseInt(random(1, 5));
    float sh_speed = random(0.5f, 3);
    int offset = PApplet.parseInt(random(0, 180));
    boolean active = false;

    float ease = 0.05f;
    float movespeediv = 20;

    public ShimmerPt(PVector loc, float dist){
        start = loc.copy();
        moving = loc.copy();
        target = loc.copy();
        wavy = loc.copy();
        end = loc.copy();
        end.y -= dist/random(2, 7);
        amp /= 3;
        secondaryamp /= 3;
    }

    public void activate(){
        active = true;
    }
    public void deactivate(){
        active = false;
    }

    public void shimmer(){
        if (active) {
            moveto();
        } else {
            moveback();
        }
        float dy = target.y - moving.y;
        moving.y += dy * ease;
        float curramp = amp - secondaryamp * sin(radians(frameCount - offset));  
        wavy.y = moving.y + curramp * sin(radians(frameCount) * sh_speed);
    }

    public void display(){
        fill(255, 0, 0);
        ellipse(target.x, target.y, 5, 5);
    }

    public void moveto() {
        ease = 0.05f;
        if (target.y > end.y) {
            target.y = end.y;
        }
    }

    public void moveback() {
        ease = 0.01f;
        if (target.y < start.y) {
            target.y = start.y;
        }
    }
}
  public void settings() {  size(512, 512); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PaperWave" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
