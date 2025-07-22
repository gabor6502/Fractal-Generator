package SystemOperators;

import DataObjects.ColourRGB;
import DataObjects.ComplexNumber;
public class FractalGenerator {
    
    //wrapper for function pointers
    private interface FractalFunction{void fractal(ComplexNumber z, ComplexNumber c);}
    private interface _FractalFunction{void _fractal(ComplexNumber _z, ComplexNumber z);}
    
    //**********ENUMS***********
    public enum Fractals{Mandelbrot, BurningShip}
    
    //**********CONSTANTS**********
    protected static final int R_SQ = 4; //bounds of mandelbrot set, squared
    private static final int R_LARGE = 100*100; //needed for other colouring methods
    //**********INSTANCE VARIABLES********
    private FractalFunction fracfunc;
    private _FractalFunction _fracfunc;
    private Fractals currFrac;
    
    //**********CONSTRUCTORS**************
    public FractalGenerator(){
        fracfunc = this::mandelbrot; //init value
        _fracfunc=this::_mandelbrot;
        
        currFrac = Fractals.Mandelbrot;
    }//end init constructor
    
    //*********ACCESSESORS***************
    protected int getCurrentFractalIndex(){
        int index=0;
        
        //switch used instead of if just to support addition of more fractals
        switch(currFrac){
            /*case Mandelbrot: index=0;
              break;*/
            case BurningShip: index=1;
              break;
        }//end switch
        
        return index;
    }//end getCurrentFractalIndex
    //*********MUTATORS***************
    //enum param
    public void setFractal(Fractals f){
        
        switch(f){
            case BurningShip -> {
                fracfunc = this::burningShip;
                _fracfunc = this::_burningShip;
            }//end burning ship case
            case Mandelbrot -> {
                fracfunc = this::mandelbrot;
                _fracfunc = this::_mandelbrot;
            }//end mandelbrot case
        }//end swtich
        
        currFrac=f; //set the fractal enum
        
    }//end setFractal
    
    //overloaded int param
    public void setFractal(int indexF){
        Fractals fractal;
        
        try{
            fractal = Fractals.values()[indexF]; //get fractal at index
            setFractal(fractal); //set it
        }catch(IndexOutOfBoundsException e){
            
        }//end try catch
        
    }//end overloaded setfractal
    
    //**********FRACTAL GENERATION METHODS**********
  
    //white background, black set
    public void white(int iterations, ColourRGB target, ComplexNumber c, ComplexNumber z){
        z.assign(0,0); //reset z to z0
        target.assign(0, 0, 0); //reset to black for inside set

          for(int n=0; n < iterations; n++){
              if(z.modulusSq() > R_SQ){//if escapes
                  target.assign(1, 1, 1); //set target pixel to white
                  break; //get out of here asap
              }//end if
            
              //iterate the fractal's function
              fracfunc.fractal(z,c);
          }//end for

    }//end white
            
            
    //NOTE: these colouring ways and implementations are from a walkthrough I did online here:
    //https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set
    //the optimizations with my objects and pointers were done by me, but the ways to acheive the results in general are not my ideas
    //although, I hope later on in this file there are other colouring methods that I come up with myself  :)
    public void escapeTimes(int iterations, ColourRGB target, ComplexNumber c, ComplexNumber z, 
            int palLen, float r, float g, float b){ //colours with escape times
        
        z.assign(0,0); //reset z to z0
        target.assign(0, 0, 0); //reset to black for inside set
        
        for(int n=0; n < iterations; n++){
            if(z.modulusSq() > R_SQ){//if escapes
                target.assign((n*r/palLen) %1, (n*g/palLen) %1, (n*b/palLen) %1); //colour target pixel
                break; //get out of here asap
            }//end if
            
            //iterate the fractal's function
            fracfunc.fractal(z,c);
        }//end for
    }//end escapeTimes
    
    public void potential(int iterations, ColourRGB target, ComplexNumber c, ComplexNumber z,
            float K, double[] coefficients){
        //using this approximation: V(c)≈log|z|/2^n  
        double V; //the potential of c at z
        long power=1; //init 2^n for when n=0 
        double modSq; //saving modulus sqaured for when it's used in the calculation
        
        double x=0; //for colouring
        
        z.assign(0,0); //reset z to z0
        target.assign(1, 1, 1); //reset to white for inside set
        
        for(int n=0; n < iterations; n++){
            modSq = z.modulusSq();
            
            if(z.modulusSq() > R_SQ){//if escapes
                 V = Math.log(modSq) / (double)power; //approximate V(c)
                 x = Math.log(V)/K; //calculate x to colour with
                 
                 //colour pixel
                 target.assign((float)(1-Math.cos(coefficients[0]*x))/2, 
                               (float)(1-Math.cos(coefficients[1]*x))/2, 
                               (float)(1-Math.cos(coefficients[2]*x))/2);
                 
                break;
            }//end if
            
            //iterate the fractal's function
            fracfunc.fractal(z,c);
            
            power*=2;//multiply power
        
            if(power < 1)//correct a long overflow error
              power=1;
        }//end for
        
    }//end potential
    
    public void normalMap(int iterations, ColourRGB target, ComplexNumber c, ComplexNumber z,
            ComplexNumber _z, float lightHeight, ComplexNumber u, ComplexNumber lightVector){
        
        double modulus=1; //actual modulus of a complex number (will be sqrt'd)
        float dot=0; //dot product for light value, using lambert shading
        
        z.assign(c); //reset z to c this time
        _z.assign(1,0);//reset derrivate of z to_ z0
        u.assign(0,0); //reset to be recalculated
    
        target.assign(0, 0, 0.5f); //reset to dark blue for inside set
       
        
        for(int n=0; n < iterations; n++){
            if(z.modulusSq() > R_LARGE){//if escapes (taking bounds large at this point)
                //ratio of derrivate to function, gives equipotential
                ComplexNumber.divide(_z, z, u); 

                //"normalize" it
                modulus = Math.sqrt(u.modulusSq()+1); //u is technically a 3d vector with z coord of 1
                u.assign(u.real()/modulus, u.imaginary()/modulus);

                //get the angle of where the light is supposed to hit
                dot = (float)((u.real()*lightVector.real()) + (u.imaginary()*lightVector.imaginary()) + lightHeight);
                dot/= (1+lightHeight); //"rescale so dot <= 1"
              
                if(dot < 0) dot=0; //ensure no negative dot product, just be 0 for black
                 
                target.assign(dot,dot,dot); //assign dot product as light intensity
                break;
            }//end if
            
            //iterate
            _fracfunc._fractal(_z,z);
            fracfunc.fractal(z,c);
        }//end for
        
        
    }//end normalMap
    
    
    //OVERLOADED under development, multiple lights
     public void normalMap(int iterations, ColourRGB target, ComplexNumber c, ComplexNumber z,
            ComplexNumber _z, float [] lightHeights, ComplexNumber u, ComplexNumber [] lightVectors, float[][] intensities, ColourRGB fracColour){
        
        double modulus=1; //actual modulus of a complex number (will be sqrt'd)
        float dot=0; //dot product for light value, using lambert shading
        
        z.assign(c); //reset z to c this time
        _z.assign(1,0);//reset derrivate of z to_ z0
        u.assign(0,0); //reset to be recalculated
    
        target.assign(fracColour); //reset to whatever colour user chose for inside set
       
        
        for(int n=0; n < iterations; n++){
            if(z.modulusSq() > R_LARGE){//if escapes (taking bounds large at this point)
                ComplexNumber.divide(_z, z, u); 

                //"normalize" it
                modulus = Math.sqrt(u.modulusSq()+1); //u is technically a 3d vector with z coord of 1
                u.assign(u.real()/modulus, u.imaginary()/modulus);

                //==init first light==
                
                //get the angle of where the light is supposed to hit
                dot = (float)((u.real()*lightVectors[0].real()) + (u.imaginary()*lightVectors[0].imaginary()) + lightHeights[0]);
                dot/= (1+lightHeights[0]); //"rescale so dot <= 1"
              
                if(dot < 0) dot=0; //ensure no negative dot product, just be 0 for black
                 
                //target.assign(dot, dot, dot);
                target.assign(dot*intensities[0][0],dot*intensities[0][1],dot*intensities[0][2]);
                
                //==other lights==
                for(int i=1; i < lightVectors.length; i++){
                    //get the angle of where the light is supposed to hit
                    dot = (float)((u.real()*lightVectors[i].real()) + (u.imaginary()*lightVectors[i].imaginary()) + lightHeights[i]);
                    dot/= (1+lightHeights[i]); //"rescale so dot <= 1"
              
                    if(dot < 0) dot=0; //ensure no negative dot product, just be 0 for black
                 
                    //target.assign(dot, dot, dot);
                    target.addEq(dot*intensities[i][0],dot*intensities[i][1],dot*intensities[i][2]);
                }//end for
                //target.scalar(1f/((float)lightVectors.length));
                break;
            }//end if
            
            //iterate the fractal's function and derrivative
            //do z' = z'* f'(z) + (1+0i)
           // _z.assign((_z.real()*2*z.real()) - (_z.imaginary()*2*z.imaginary()) + 1,(_z.real()*2*z.imaginary()) + (_z.imaginary()*2*z.real()));
            _z._mandelbrot(z);
            fracfunc.fractal(z,c);
        }//end for
        
        
    }//end normalMap

    private void mandelbrot(ComplexNumber z, ComplexNumber c){
        //call mandelbrot function
        z.mandelbrot(c);
    }//end mandelbrot
    
    private void burningShip(ComplexNumber z, ComplexNumber c){
        //perform burning ship function
        z.burningShip(c);
    }//end burningShip
    
    private void _mandelbrot(ComplexNumber _z, ComplexNumber z){
        _z._mandelbrot(z);
    }//end mandelbrot derrivate
    
    private void _burningShip(ComplexNumber _z, ComplexNumber z){
      _z._burningShip();   
    }//end burning ship derrivative, under development
    
}//end class