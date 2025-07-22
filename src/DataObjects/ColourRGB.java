package DataObjects;

public final class ColourRGB {//3d vector but used in the context of displaying and storing a colour of a pixel in the fractal
    
    //********INSTANCE VARIABLES********
    private float red,green,blue;
    
    //********CONSTRUCTORS********
    public ColourRGB(){
        red=0;
        green=0;
        blue=0;
    }//end default constructor
    
    public ColourRGB(float r, float g, float b){
        red=r;
        green=g;
        blue=b;
    }//end init constructor
    
    public ColourRGB(ColourRGB colour){
        assign(colour);
    }//end clone constructor
    
    //********ACCESSORS***********
    public float red(){
        return red;
    }//end red
    
    public float green(){
        return green;
    }//end green
    
    public float blue(){
        return blue;
    }//end blue
    
    //*********MUTATORS*********
    //the instance is an operand, did this for speed and memory saving.
    //don't want to be calling new all the time during antialiasing
    
    //does +=
    public void addEq(ColourRGB right){
        red+=right.red();
        green+=right.green();
        blue+=right.blue();
    }//end addEq
    
    public void addEq(float r, float g, float b){
        red+=r;
        green+=g;
        blue+=b;
    }
    
    //does sclar mult on a 3d vector
    public void scalar(float k){
        red*=k;
        green*=k;
        blue*=k;
    }//end scalar
    
    //does =
    public void assign(ColourRGB right){
        red = right.red();
        green = right.green();
        blue = right.blue();
    }//end assign
    
    public void assign(float r, float g, float b){
        red = r;
        green = g;
        blue = b;
    }//end assign overloaded
    
}//end class