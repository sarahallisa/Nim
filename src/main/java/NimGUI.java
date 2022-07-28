// IMPORTANT LESSON!
// Die Oberfläche hat alle die Informationen, die notwendig ist für die Oberfläche.

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.Arrays;
import java.util.Random;

public class NimGUI extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new NimGUI());
    }
    
    NimInterface n;
    int[] rows = {9, 9, 9, 9, 9, 9};
    int sum;
    Stick[] stick;
    TextBox text;
    Button remove, computer, undo;
    String typing;
    int x, y;
    int playingRow;
    int screen = 0;

    public void settings() {
        size(500, 550);
    }

    public void setup() {
        n = Nim.of(randomSetup(rows));
        
        createStick();

        text = new TextBox();
        typing = "";

        remove = new Button(400, 280);
        computer = new Button(400, 100);
        undo = new Button(400, 450);
    }

    // Creates a whole newly updated array of sticks 
    public void createStick() {
        sum = Arrays.stream(((Nim)n).rows).sum();
        stick = new Stick[Arrays.stream(((Nim)n).rows).sum()];

        x = 50;
        y = 30;
        int k = 0;
        for (int i = 0; i < ((Nim)n).rows.length; i++) {
            for (int j = 0; j < ((Nim)n).rows[i]; j++) {
                stick[k] = new Stick(x, y, i);
                x = x + 30;
                k++;
            }
            y = y + 80;
            x = 50;
        }
    }

    // Retrieve the row number from the inserted user text and save it to playingRow
    public void keyPressed() {
        typing+=key;

        if (keyCode == ENTER) {
            typing = typing.substring(0, typing.length() - 1);
            int saved = 0;
            try {
                saved = Integer.parseInt(typing);
            } catch (NumberFormatException nfe) {
                
            }
            playingRow = saved - 1;
            typing = "";      
        }
    }

    public void draw() {
        // Opening layout
        if (screen == 0) {
            background(69, 144, 89);
            fill(255);
            textAlign(CENTER);
            textSize(70);
            text("NIM!", width/2, height/2);
            textSize(25);
            text("left click anywhere to start", width/2, (height/2) + 40);

            if (mousePressed == true && mouseButton == LEFT) {
                screen = 1;
            }
        }

        // In-Game layout
        else if (screen == 1) {
            background(175, 210, 185);
        
            text.draw(g);

            remove.mousePress();
            computer.mousePress();
            undo.mousePress();

            fill(255);
            remove.draw(g);
            fill(0);
            textSize(20);
            text("Move!", 400, 287);

            fill(255);
            computer.draw(g);
            fill(0);
            textSize(20);
            text("COMP", 400, 107);

            fill(255, 127, 127);
            undo.draw(g);
            fill(0);
            textSize(20);
            text("Undo!", 400, 457);

            // Create numbers on the left screen for each row
            int y = 60;
            for (int i = 0; i < ((Nim)n).rows.length; i++) {
                fill(0);
                text((i + 1) + ".", 20, y);
                y = y + 80;
            }

            // Check if the user input number doesn't pass the given row limit
            if (playingRow + 1 > ((Nim)n).rows.length) {
                textSize(20);
                text("invalid number of rows!", 180, 532);
            }

            // Draw all the sticks
            for (int i = 0; i < sum; i++) {
                if(stick[i].rows == playingRow) {
                    stick[i].mousePress();
                }
                stick[i].draw(g);
            }

            // The user's turn to play
            if (remove.selected) {
                int count = 0;

                // Interact with sticks at the user selected row and count the number of sticks selected 
                for (int i = 0; i < sum; i++) {
                    if (stick[i].selected && stick[i].rows == playingRow) {
                        count++;
                    }              
                }
                
                // Play the number of sticks selected at the user selected row and create an updated version of sticks
                if (count > 0) {
                    n = n.play(Move.of(playingRow, count));
                    createStick();
                    println(n.toString());
                }
            }
            
            // The computer's turn to play
            if (computer.selected) {
                n = n.play(n.nimPerfect());
                createStick();
                println(n.toString());
                computer.selected = false;
            }

            // Go back to the previous game state
            if (undo.selected) {
                if (n.historyEmpty()) {
                    fill(1);
                    textSize(20);
                    text("nothing to undo!", 180, 532);
                } else {
                    n = n.undo();
                    createStick();
                    println(n.toString());
                    undo.selected = false;
                }
            }

            if (n.isGameOver()) screen = 2;
        }

        // Game Over layout
        else if (screen == 2) {
            background(59, 100, 70);
            fill(255);
            textAlign(CENTER);
            textSize(70);
            text("GAME OVER", width/2, height/2);

            textSize(25);
            text("right click anywhere to play again", width/2, (height/2) + 40);
            
            if (mousePressed == true && mouseButton == RIGHT) {
                screen = 1;
                setup();
            }
        }
    }

    class Stick {
        int x, y;
        boolean pressed = false;
        boolean selected = false;
        int colorRect = color(193, 154, 107);
        int colorEllipse = color (139, 0, 0);
        int rows;

        Stick(int x, int y, int rows) {
            this.x = x;
            this.y = y;
            this.rows = rows;
        }

        void draw(PGraphics g) {
            g.fill(colorRect);
            g.rect(x, y, 10, 60);
            g.fill(colorEllipse);
            g.ellipse(x + 5, y + 5, 15, 20);
        }

        void mousePress() {
            if (mousePressed == true && mouseButton == LEFT && pressed == false) {
                pressed = true;
                if (mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 60 + 20 && selected == false) {
                    selected = true;
                    colorEllipse = color(249, 215, 28);
                }
                else if (mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 60 + 20 && selected == true) {
                    selected = false;
                    colorEllipse = color (139, 0, 0);
                }
            }
            if (mousePressed != true) {
                pressed = false;
            }
        }
    }

    class TextBox {

        TextBox() {

        }

        void draw(PGraphics g) {
            g.fill(255);
            g.rect(375, 355, 50, 30);
            
            g.fill(0);
            g.textSize(15);
            g.text("Type the row to select!", 400, 350);
            g.textSize(25);
            g.text(typing, 400, 380);
        }
    }

    class Button {
        boolean pressed = false;
        boolean selected = false;
        int rows;
        int x;
        int y;
        int r = 60;

        Button(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void draw(PGraphics g) {
            g.ellipse(x, y, r, r);            
        }

        void mousePress() {
            if (mousePressed == true && mouseButton == LEFT && pressed == false) {
                pressed = true;
                if (mouseX >= x - 60 && mouseX <= x + 60 && mouseY >= y - 60 && mouseY <= y + 60 && selected == false) {
                    selected = true;
                }
                else {
                    selected = false;
                }
            }
            if (mousePressed != true) {
                pressed = false;
            }
        }
   }

    int[] randomSetup(int... maxN) {
        Random r = new Random();
        int[] rows = new int[maxN.length];
        for (int i = 0; i < maxN.length; i++) {
            rows[i] = r.nextInt(maxN[i]) + 1;
        }
        return rows;

    }
}