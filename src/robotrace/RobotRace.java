package robotrace;

import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_MULTISAMPLE;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL2.*;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards;
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the same folder as this
 * file. These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {
    
    /** Array of the four robots. */
    private final Robot[] robots;
    
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instance of the race track. */
    private final RaceTrack[] raceTracks;
    
    /** Instance of the terrain. */
    private final Terrain terrain;
    
    /**
     * Constructs this robot race by initialising robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        
        // Create a new array of four robots
        robots = new Robot[4];
        
        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD,
                              new Vector(0f, 0f, 0f),
                              "wall-e", "wall-e", "wall-e", "wall-e");
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER,
                              new Vector(0f, 0f, 0f),
                              "tracks", "aaa", "thin", "aaa");
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD,
                              new Vector(0f, 0f, 0f),
                              "tracks", "aaa", "thin", "aaa");

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE,
                              new Vector(0f, 0f, 0f),
                              "tracks", "aaa", "thin", "aaa");
        
        // Initialize the camera
        camera = new Camera();
        
        // Initialize the race tracks
        raceTracks = new RaceTrack[5];
        
        // Test track
        raceTracks[0] = new RaceTrack();
        
        // O-track
        raceTracks[1] = new RaceTrack(new Vector[] {
            /* add control points like:
            new Vector(10, 0, 1), new Vector(10, 5, 1), new Vector(5, 10, 1),
            new Vector(..., ..., ...), ...
            */
        });
        
        // L-track
        raceTracks[2] = new RaceTrack(new Vector[] { 
            /* add control points */
        });
        
        // C-track
        raceTracks[3] = new RaceTrack(new Vector[] { 
            /* add control points */
        });
        
        // Custom track
        raceTracks[4] = new RaceTrack(new Vector[] { 
           /* add control points */
        });
        
        // Initialize the terrain
        terrain = new Terrain();
    }
    
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {
        
        //Enable Shading
        /*
        float[] lightPos = {2.0f, 0.0f, 3.0f, 0.0f};
        float[] whiteColor = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] pinkColor = {1.0f, 0.5f, 0.5f, 1.0f};
        gl.glShadeModel(GL_SMOOTH); // Use smooth shading
        gl.glEnable(GL_LIGHTING); // Enable lighting
        gl.glEnable(GL_LIGHT0); // Enable light source #0
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPos, 1); // position LS 0
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, whiteColor, 1); // set color LS 0
		
        gl.glMaterialfv(GL_FRONT, GL_AMBIENT, pinkColor, 1);
        */
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
		
        // Normalize normals.
        gl.glEnable(GL_NORMALIZE);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
		
	    // Try to load four textures, add more if you like.
        track = loadTexture("track.jpg");
        brick = loadTexture("brick.jpg");
        head = loadTexture("head.jpg");
        torso = loadTexture("torso.jpg");
        
        gs.vDist = 40;
        gs.theta = (float)(0.25 * Math.PI);
        gs.phi = (float)(0.20 * Math.PI);
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        glu.gluPerspective(gs.vWidth, (float)gs.w / (float)gs.h,  0.1*gs.vDist, 10.0*gs.vDist);
        
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        
        camera.update(gs, robots[0]);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
        
    }
    
    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);
        
        // Get the position and direction of the first robot.
        robots[0].position = raceTracks[gs.trackNr].getLanePoint(0, 0);
        robots[0].direction = raceTracks[gs.trackNr].getLaneTangent(0, 0);
        
        // Draw the first robot.
        robots[0].draw(gl, glu, glut, false, gs.tAnim);
        
        // Draw the race track.
        raceTracks[gs.trackNr].draw(gl, glu, glut);
        
        // Draw the terrain.
        terrain.draw(gl, glu, glut);
        
        gl.glColor3f(0f, 0f, 0f);
        
        // Unit box around origin.
        glut.glutWireCube(1f);

        // Move in x-direction.
        gl.glTranslatef(2f, 0f, 0f);
        
        // Rotate 30 degrees, around z-axis.
        gl.glRotatef(30f, 0f, 0f, 1f);
        
        // Scale in z-direction.
        gl.glScalef(1f, 1f, 2f);

        // Translated, rotated, scaled box.
        glut.glutWireCube(1f);
    }
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        gl.glColor3f(1f, 1f, 0f);
        glut.glutSolidSphere(0.1f, 20, 20);
        //---------------------------
        gl.glPushMatrix();
        
        gl.glColor3f(1f, 0f, 0f);
        
        gl.glRotatef(90f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, 0.9f);
        glut.glutSolidCone(0.05f, 0.1f, 10, 1);
        gl.glScalef(0.01f, 0.01f, 1f);
        gl.glTranslatef(0f, 0f, -0.4f);
        
        glut.glutSolidCube(1f);
        
        
        gl.glPopMatrix();

        //--------------------------
        gl.glPushMatrix();
        
        gl.glColor3f(0f, 1f, 0f);
        
        gl.glRotatef(-90f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, 0.9f);
        glut.glutSolidCone(0.05f, 0.1f, 10, 1);
        gl.glScalef(0.01f, 0.01f, 1f);
        gl.glTranslatef(0f, 0f, -0.4f);
        
        glut.glutSolidCube(1f);
        
        gl.glPopMatrix();
        //--------------------------
        gl.glPushMatrix();
        
        gl.glColor3f(0f, 0f, 1f);
        
        gl.glTranslatef(0f, 0f, 0.9f);
        glut.glutSolidCone(0.05f, 0.1f, 10, 1);
        gl.glScalef(0.01f, 0.01f, 1f);
        gl.glTranslatef(0f, 0f, -0.4f);
        
        glut.glutSolidCube(1f);
        
        gl.glPopMatrix();
    }
 
    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    } 
}
