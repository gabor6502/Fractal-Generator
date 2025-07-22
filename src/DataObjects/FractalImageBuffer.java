package DataObjects;

public class FractalImageBuffer {
    
    //********INSTANCE VARIABLES********
    private ColourRGB[][] buffer; //2d array of colour vectors
    
    //********CONSTRUCTORS********
    public FractalImageBuffer(int dimension){
        buffer = new ColourRGB[dimension][dimension];
        
        //init all pointers to colourRGB objects
        for (ColourRGB[] row : buffer) 
            for (int j = 0; j < row.length; j++) 
                row[j] = new ColourRGB();

    }//end init constructor
    
    //********ACCESSORS***********
    public int getDimension(){
        return buffer.length;
    }//end getDimension
    
    public ColourRGB get(int r, int c){//soft copy
        return buffer[r][c];
    }//end get
    
    //for averaging the image, in the case of supersampling
    public ColourRGB averageColour(){
        ColourRGB result = this.sumChannels();
        result.scalar(1f/((float)buffer.length*(float)buffer.length)); //perform average on sums
        
        return result;
    }//end average colour
    
    //sums the values of all channels
    public ColourRGB sumChannels(){
        ColourRGB result; //returns this
        float r=0,g=0,b=0; //averages of each channel
        
        for(ColourRGB[] row : buffer)
            for(ColourRGB colour : row){
                //summation
                r+=colour.red();
                g+=colour.green();
                b+=colour.blue();
            }//end for
        
        result = new ColourRGB(r,g,b); //create new colour object with individual channel sums

        return result;
    }//end sum colour
    
}//end class