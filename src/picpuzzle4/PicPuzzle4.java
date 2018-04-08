/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picpuzzle4;
//package Game;
import com.sun.javafx.scene.control.skin.Utils;
import javax.swing.*;               // GUI
import java.awt.*;                  // GUI
import java.awt.event.*;            // Events like ActionListener
import java.awt.image.*;            // BufferedImage
import javax.imageio.ImageIO;       // Work with images
import java.io.*;                   // Input-Output-Files-Console
import java.util.Random;            // Random numbers
import java.net.URL;                // Working with URLS
import javax.swing.JOptionPane;     // for MessageBox


/**
 *
 * @author sahil
 */
public final class PicPuzzle4 extends Panel implements MouseListener{

    public static BufferedImage pic;        // Picture
    public int pieceW, pieceH;              // Width and Height of pieces
    
    //settings
    public int pieceNumX = 3;
    public int pieceNumY = 3;
    public int marginX = 10;
    public int marginY = 10;
    
    private int sel, selected = 0;
    public int tries = 0;
    
    // board array (once for pics, one for positions)
    /* 1  2  3  4
     * 5  6  7  8
     * 9  10 11 12
     * 13 14 15 16
     */
    public Image    set[][];
    public int      pos[][];
    public int imageNum = 0;
    // seed Random
    public Random rand = new Random();
    
    // game states
    public boolean initField = false;
    
    
    public PicPuzzle4()
    {
        addMouseListener(this);
        
        //System.out.print("Do you want to use an URL (u) or a file (f)? ");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            //String input = reader.readLine();
            
            //if ("u".equals(input))
                //pic = ImageIO.read(new URL("https://github.com/SahilQaiser/PicPuzzle/raw/master/images/main4.jpg"));
                
            //else if ("f".equals(input))
            //InputStream stream = this.getClass().getClassLoader().getResourceAsStream("/picpuzzle4/images/main4.jpg");
            //pic = ImageIO.read(getClass().getResource(stream.toString()));
            pic = ImageIO.read(getClass().getResource("/picpuzzle4/images/main4.jpg"));
            imageNum = 1;
            //pic = ImageIO.read(new File("images\\main4.jpg"));
            //else
                //System.exit(-1);
        } 
        catch (IOException io)
        {
            System.out.println(io.getLocalizedMessage());
            System.exit(-1);
        }
        
        initFields();
        Shuffle();
        // TODO code application logic here
    }
    
    
    // Helper functions
    int PosToIndexX(int pos)
    {
        return ( (pos % pieceNumX) == 0 ? pieceNumX -1 : (pos % pieceNumX) -1 );
    }
    
    int PosToIndexY(int pos)
    {
        return (int)( (pos % pieceNumX) == 0 ? (pos/pieceNumX) -1 : Math.ceil((double)(pos / pieceNumX)));
    }
    
    int getPosFromScreen(int posX, int posY)
    {
        int k = ((posX-marginX)/pieceW);
        k += ((posY-marginY)/pieceH)*pieceNumX;
        return k+1;  // position, not index, thus +1
    }
    // ~ Helper functions
    
    
    // Class methods here
    public void initFields()
    {
        if (pieceNumX <= 1 && pieceNumY <= 1)
        {
            System.err.println("Error! Column and row size have to be greater than 1.");
            System.exit(-1);
        }
        
        // calc size of pieces
        pieceW = pic.getWidth() / pieceNumX;
        pieceH = pic.getHeight() / pieceNumY;
        
        // init arrays
        set = new Image[pieceNumX][pieceNumY];
        pos = new int[pieceNumX][pieceNumY];
        
        ImageFilter filter;
        
        int total = 1;
        for (int j = 0; j < pieceNumY; j++)
        {
            for (int i = 0; i < pieceNumX; i++)
            {
                filter = new CropImageFilter(i*pieceW, j*pieceH, pieceW, pieceH);
                set[i][j] = createImage(new FilteredImageSource(pic.getSource(), filter));
                pos[i][j] = total++;
            }
        }
        initField = true;
    }
    
    public void next()
    {
        try{
            if(imageNum==1){
            //pic = ImageIO.read(new URL("https://github.com/SahilQaiser/PicPuzzle/raw/master/images/main3.jpg"));
            pic = ImageIO.read(getClass().getResource("/picpuzzle4/images/main3.jpg"));
            imageNum = 2;
            }
            else {
                pic = ImageIO.read(getClass().getResource("/picpuzzle4/images/main4.jpg"));
               //pic = ImageIO.read(new URL("https://github.com/SahilQaiser/PicPuzzle/raw/master/images/main4.jpg"));
               imageNum = 1;
            }
        } catch (IOException io)
        {
            System.out.println(io.getLocalizedMessage());
            System.exit(-1);
        }
        if(imageNum == 2){
            pieceNumY = 2;
        }else {
            pieceNumY = 3;
        }
        initFields();
        Shuffle();        
    }
    
    public void Shuffle()
    {
        if (!initField)
            initFields();
        // init arrays
        
        int total = pieceNumX*pieceNumY;
        
        for (int i = 0; i < total; i++)
        {
            //nextInt -> 0 - int
            int rnd1 = rand.nextInt(total-1)+1;
            int rnd2 = rand.nextInt(total-1)+1;
            Swap(rnd1, rnd2);
        }
        
        if (GameIsWon())
            Shuffle();
        
        repaint(); 
        tries = 0;
    }
    
    public void Swap(int a, int b)
    {
        try
        {
            int aX      = PosToIndexX(a);
            int aY      = PosToIndexY(a);
            int bX      = PosToIndexX(b);
            int bY      = PosToIndexY(b);

            int tmp                             =   pos[aX][aY];
            Image tmp_pic                       =   set[aX][aY];

            pos[aX][aY] =   pos[bX][bY];
            set[aX][aY] =   set[bX][bY];

            pos[bX][bY] =   tmp;
            set[bX][bY] =   tmp_pic;
        } 
        catch (Exception e)
        {
            System.out.println(e.getLocalizedMessage());
        }
        
        tries += 1;
    }
    
    public void Solve()
    {
        initFields();
        repaint();
    }
    
    public boolean GameIsWon()
    { 
        boolean won = true;
        int index = 1;
        for (int j = 0; j < pieceNumY; j++)
        {
            for (int i = 0; i < pieceNumX; i++)
            {
                if (pos[i][j] != index)
                {
                    won = false;
                    break;
                }
                
                index += 1;
            }
        }
        return won;
    }
    
    public String getCorrect()
    {
        int correct = 0;
        int index = 1;
        for (int j = 0; j < pieceNumY; j++)
        {
            for (int i = 0; i < pieceNumX; i++)
            {
                if (pos[i][j] == index)
                    correct += 1;
                index += 1;
            }
        }
        return correct + "\\" + pieceNumX*pieceNumY;
    }
    
    @Override 
    public void paint(Graphics g)
    {
        for (int j = 0; j < pieceNumY; j++)
        {
            for (int i = 0; i < pieceNumX; i++)
            {
                g.drawImage(set[i][j], i*pieceW + marginX, j*pieceH + marginY, null);
            }
        }
        
        // blue box
        if (selected != 0)
        {
            g.setColor(Color.blue);
            g.drawRect(PosToIndexX(sel)*pieceW + marginX, PosToIndexY(sel)*pieceH + marginY, pieceW, pieceH);
            g.drawRect(PosToIndexX(sel)*pieceW + marginX -1, PosToIndexY(sel)*pieceH + marginY -1, pieceW, pieceH);
        }
    }
    
    // mouse listener
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (GameIsWon())
            return;
        sel = getPosFromScreen(e.getX(), e.getY());
        
        if (selected != 0)
        {
            if (sel != selected)
            {
                Swap(sel, selected);
                selected = 0;
            }
            else
                selected = 0;
        }
        else
            selected = sel;
        // repaint the applet 
        repaint();      
        if (GameIsWon())
        {
            JOptionPane.showMessageDialog(null, "You have won the game. You moved the pieces " + tries + " times." , "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    } 
   
    // dont use these so leave them empty 
   
   @Override
   public void mouseClicked(MouseEvent e){} 
   @Override
   public void mouseEntered(MouseEvent e){} 
   @Override
   public void mouseExited(MouseEvent e){} 
   @Override
   public void mouseReleased(MouseEvent e){}
    
   //global function
   public static Dimension getPicSize()
   {
       return new Dimension(pic.getWidth() + 20, pic.getHeight() + 20);
   }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //change frame look
        JFrame.setDefaultLookAndFeelDecorated(false);
        //create frame
        JFrame frame = new JFrame("PicPuzzle 1.0");
        //set close action
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // GAME PANEL
        Panel pan = new PicPuzzle4();
                
        final PicPuzzle4 Puzzle = (PicPuzzle4)pan;
        pan.setPreferredSize(PicPuzzle4.getPicSize());      
        frame.add(pan);
                
        // BUTTON PANEL
        Panel pan_btn = new Panel();
        
        final JLabel points = new JLabel("");        
        points.setText("Correct: " + Puzzle.getCorrect());
        pan_btn.add(points);
        
        pan.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent me) 
            { 
                points.setText("Correct: " +Puzzle.getCorrect());
            } 
        });
        JButton next = new JButton("Next Puzzle");
        next.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){  
                Puzzle.next();
            }
        });
        next.setSize(30,20);
        pan_btn.add(next);
        
        JButton shuffle = new JButton("Shuffle");
        shuffle.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){  
                Puzzle.Shuffle();
            }
        });
        
        shuffle.setSize(30, 20);
        pan_btn.add(shuffle);   
        
        JButton solve = new JButton("Solve");
        solve.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){  
                Puzzle.Solve();
            }
        });
        
        solve.setSize(30, 20);
        pan_btn.add(solve);   
        
        
        JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){  
                System.exit(0);
            }
        });
        
        shuffle.setSize(30, 20);
        pan_btn.add(exit);   
        
     
        frame.add(pan_btn, BorderLayout.SOUTH);
        
        //set size automatically
        frame.pack();
        
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;

        // Move the window
        frame.setLocation(x, y);
        //show frame
        frame.setVisible(true);   
    }
}
