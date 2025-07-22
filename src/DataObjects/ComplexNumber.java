package DataObjects;

public final class ComplexNumber {
    
    //********INSTANCE VARIABLES********
    private double real; //real part
    private double imaginary; //imaginary part
    
    //********CONSTRUCTORS********
    public ComplexNumber(){
        real = 0;
        imaginary = 0;
    }//end default constructor
    
    public ComplexNumber(double r, double i){
        real = r;
        imaginary = i;
    }//end init constructor
    
    public ComplexNumber(ComplexNumber c){
        assign(c);
    }//end clone constructor
    
    public static void divide(ComplexNumber numerator, ComplexNumber denominator, ComplexNumber target){
        //https://www.cuemath.com/numbers/division-of-complex-numbers/
        
        double div = denominator.modulusSq(); //divisor
        
        //ac+bd
        target.setReal( ((numerator.real()*denominator.real()) + 
                         (numerator.imaginary()*denominator.imaginary())) 
                        /div);
        
        //bc-ad
        target.setImaginary( ((numerator.imaginary()*denominator.real()) - 
                              (numerator.real()*denominator.imaginary())) 
                            /div);
    }//end divide
    
    
    //********ACCESSORS***********
    public double real(){
        return real;
    }//end real
    
    public double imaginary(){
        return imaginary;
    }//end imaginary
    
    @Override
    public String toString(){
        return real+" + i"+imaginary;
    }//end toString
    
    //an optimization under development
    public boolean cardiodBulbCheck(){
        
        double q = (real+0.25)*(real+0.25)+(imaginary*imaginary);
        
        boolean result = q*(q+(real-0.25)) < imaginary*imaginary*0.25;
        
        if(result) //if cardiod test passed, do bulb
            result = (real+1)*(real+1) +(imaginary*imaginary) <= 0.0625;
        
        return result;
    }//end cardiodBulbCheck
    
    //*********MUTATORS*********
    public void setReal(double r){
        real = r;
    }//end setReal
    
    public void setImaginary(double i){
        imaginary = i;
    }//end setImaginary
    
    //assigns values of a complex number to this instance, like =
    public void assign(ComplexNumber right){//"right" meaning right side of =
        real = right.real();
        imaginary = right.imaginary();
    }//end assign
    
    public void assign(double r, double i){
        real = r;
        imaginary = i;
    }//end overloaded assign
    
    public void add(double r, double i){
        real += r;
        imaginary += i;
    }//end add
    
    public void add(ComplexNumber c){
        real += c.real();
        imaginary += c.imaginary();
    }//end overloaded add
    
    //may need for other colouring methods
    public double argument(){
        return Math.atan2(imaginary, real);
    }//argument
    
    //returns |z|^2
    public double modulusSq(){
        return (real*real) + (imaginary*imaginary);
    }//end modulusSq
    
    //returns |z|
    public double modulus(){
        return Math.sqrt(modulusSq());
    }//end modulus
    
    //complex number object does fractal math on itself too

    public void mandelbrot(ComplexNumber c){
        double nr = ((real*real) - (imaginary*imaginary)) + c.real(); //new real
        double ni = (2 * real*imaginary) + c.imaginary(); //new imaginary
        
        real = nr;
        imaginary = ni;
    }//end mandelbrot function
    
    public void burningShip(ComplexNumber c){
        if(real < 0) real *= -1; // |Re(z)|
        if(imaginary < 0) imaginary *= -1; // |Im(z)|
        
        mandelbrot(c); //(|Re(z)| +i|Im(z)|)^2 + c
    }//end burningShip function
    
    //derrivative of mandelbrot function
    public void _mandelbrot(ComplexNumber z){
        //do z' = z'* f'(z) + (1+0i) to get desried results
        
        //calculate derrivative
        double nr = (real*2*z.real()) - (imaginary*2*z.imaginary()) + 1;
        double ni = (real*2*z.imaginary()) + (imaginary*2*z.real());
        
        //set result
        real =nr;
        imaginary =ni;
    }//end mandelbrot'
    
    public void _burningShip(){
        //let z_n = a+bi
        //since |x| = sqrt(sq(x)) kinda, a lot of people online say |x|' = x/|x|
        //z_n+1 = (|a| + |b|)^2 + c
        //(z_n+1)' = (2|a| + 2|b|) + a/|a| + b/|b|
        //-> intresting to note that a/|a| is 1 if a is +ve or -1 if a is -ve
        //to cut down on divisions we can just use some logic
        
        //new real and imaginary
        double nr;
        double ni;
        
        //the ratios of values to their absolute values
        double absR=0; //a/|a|
        double absI=0; //b/|b|
        
        //sign check
        if(real > 0)
            absR=1;
        else
            absR=-1;
        
        if(imaginary > 0)
            absI=1;
        else
            absI=-1;
        
        //calculate derrivative
        nr = (2*Math.abs(real))+absR;
        ni = (2*Math.abs(imaginary))+absI;
        
        //set result
        real=nr;
        imaginary=ni;
    }//end burningShip'
    
    
}//end class
