package SystemOperators;

import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import processing.event.MouseEvent;

import SystemOperators.Controller.ViewModes;
public class InputHandler {
    
    //*********CONSTANTS*************
    //caps lock conversion int
    private final int CAPS = 32;
    
    //key list
    private final char ZOOM_IN = '=';
    private final char ZOOM_OUT= '-';
    
   // private final char ZOOM_M = 'z'; //manual zoom entry[replaced with button on GUI]
    //private final char SAVE = 's'; //save the image on screen [replaced with button on GUI]
    private final char AA = 'a'; //toggle antialiasing
    private final char GUI ='g'; //bring up the gui
    private final char HELP = 'h'; //bring up help 

    //*********VARIABLES*************
    private Controller controller;
    private Renderer renderer;
    
    //**********CONSTRUCTORS**********
    public InputHandler(Controller c, Renderer r){
        controller = c;
        renderer = r;
    }//end init constructor
    
    //*********KEY HANDLING METHODDS**************
    public boolean handleKeyPress(char key){ //returns if need to call redraw
        boolean redraw = key == ZOOM_IN || key == ZOOM_OUT 
                      || key == AA      || key == AA-CAPS;
        
        if(controller.getViewMode() == ViewModes.full){//only do key stuff when in full mode
            switch(key){
                
                case ZOOM_IN -> controller.zoomIn();
                case ZOOM_OUT -> controller.zoomOut();
                case AA, AA-CAPS -> controller.toggleAA();
                case GUI, GUI-CAPS -> controller.viewGUI(); 
                case HELP, HELP-CAPS -> controller.helpMessage();

             }//end switch
        }//end if
        
        return redraw;
    }//end handleKeyPress
    
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton){//have these passed in case they change during evaluation
        
        switch(mouseButton){
            
            case LEFT -> {//handle left button
                switch(controller.getViewMode()){
                    
                    case full -> {
                        controller.setToMousePos(mouseX, mouseY, renderer.width, renderer.height);
                    }//end full case
                    
                    case preview -> {
                        controller.setViewMode(Controller.ViewModes.transition); //transition from preview to full
                        renderer.setupForTransitionMode();
                        //save mouse coords to translate too before we zoom
                        controller.preaprePreviewTranslateZoom(mouseX, mouseY);
                    }//end preview case
                            
                    case transition -> { //transformed, render in full
                        controller.engagePreviewTranslateZoom(renderer.width, renderer.height);
                        controller.setViewMode(Controller.ViewModes.full);
                    }//end transition case
                   
                }//end nested switch
            }//end left case
            
            case RIGHT -> {//handle right button
                switch(controller.getViewMode()){
                    
                    case full, transition -> { //right click in full mode means go to preview mode, same for transitioning to new image/zoom depth from preview 
                        renderer.setupForPreviewMode();
                        controller.setViewMode(Controller.ViewModes.preview);
                    }//end full case
                    
                    case preview ->{ //right click in preview mode means exit preview mode and go back to full
                        controller.setViewMode(Controller.ViewModes.exitPreview);
                    }//end preview case
                    
                }//end nested switch
            }//end right case
            
        }//end swtich
        
    }//end handleMouseClick
    
    public void handleMouseWheel(MouseEvent event){
        //controller handles zoom
        controller.previewZoomChange(event.getCount());
    }//end handleMouseWheel

}//end class