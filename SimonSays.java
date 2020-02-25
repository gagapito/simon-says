import tester.Tester;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

// Note: We chose to create the Simon Says game with a single subclass of World 
// because we could use this.remainingSequence field for two different functions. 
// It can be used to: (1) keeps track of the colors in the sequence that are 
// going to flash (2) to determine if the order of colors selected by the user 
// match the given pattern of colors. We also thought that putting the game 
// in a single class would be easier to implement different functions and 
// to organize the game in a cohesive way. 

class SimonSays extends World {
  ILoColor totalSequence; 
  ILoColor remainingSequence;
  boolean isGivingSequence;
  Color selectedColor;

  // default constructor 
  SimonSays(ILoColor totalSequence, ILoColor remainingSequence, 
      boolean isGivingSequence, Color selectedColor) {
    this.totalSequence = totalSequence;
    this.remainingSequence = remainingSequence;
    this.isGivingSequence = isGivingSequence;
    this.selectedColor = selectedColor;
  }

  // constructor used in the onTick() 
  SimonSays(ILoColor totalSequence, boolean isGivingSequence, Color selectedColor) {
    this.totalSequence = totalSequence;
    this.remainingSequence = totalSequence;
    this.isGivingSequence = isGivingSequence;
    this.selectedColor = selectedColor;
  }

  /*
   * TEMPLATE 
   * -------- 
   * Fields 
   * ... this.totalSequence ...                         -- ILoColor
   * ... this.remainingSequence ...                     -- ILoColor
   * ... this.isGivingSequence ...                      -- boolean
   * ... this.selectedColor ...                         -- Color
   * Methods
   * ... this.makeScene() ...                           -- WorldScene
   * ... this.colorChange(CircleImage img) ...          -- CircleImage
   * ... this.onTick() ...                              -- SimonSays
   * ... this.addSpaces() ...                           -- SimonSays
   * ... this.onMouseClicked(Posn p) ...                -- SimonSays
   * ... this.processClick() ...                        -- SimonSays
   * ... this.worldEnds() ...                           -- WorldEnd
   * ... this.makeAFinalScene() ...                     -- WorldScene
   * Methods on Fields
   * ... this.remainingSequence.matchesGivenColor() ... -- boolean 
   * ... this.remainingSequence.readyToSwitch() ...     -- boolean
   * ... this.totalSequence.addRandomColor() ...        -- ILoColor
   * ... this.remainingSequence.runSequence() ...       -- ILoColor
   * ... this.remainingSequence.addSpacesHelp() ...     -- ILoColor
   */

  // displays the circles onto the WorldScene
  public WorldScene makeScene() {
    return new WorldScene(500, 500)
        .placeImageXY(this.colorChange(
            new CircleImage(50, OutlineMode.SOLID, Color.RED)), 125, 125)
        .placeImageXY(this.colorChange(
            new CircleImage(50, OutlineMode.SOLID, Color.GREEN)), 375, 125)
        .placeImageXY(this.colorChange(
            new CircleImage(50, OutlineMode.SOLID, Color.BLUE)), 125, 375)
        .placeImageXY(this.colorChange(
            new CircleImage(50, OutlineMode.SOLID, Color.ORANGE)), 375, 375);
  }

  // changes the color of the circle that is first in the list 
  public CircleImage colorChange(CircleImage img) {
    if (this.remainingSequence.matchesGivenColor(img.color) && this.isGivingSequence) {
      return new CircleImage(50, OutlineMode.SOLID, img.color.darker().darker());
    }
    else {
      return img;
    }
  }

  // changes the color of the circle to the one next in the list
  public SimonSays onTick() {
    if (this.isGivingSequence && this.remainingSequence.readyToSwitch()) {
      return new SimonSays(this.totalSequence, this.totalSequence,
          false, this.selectedColor);
    }
    else if (this.isGivingSequence) {
      return new SimonSays(this.totalSequence, this.remainingSequence.runSequence(),
          this.isGivingSequence, this.selectedColor);
    }
    else if (!this.isGivingSequence && !this.remainingSequence.readyToSwitch()) {
      return this.processClick();
    }
    else if (!this.isGivingSequence && this.remainingSequence.readyToSwitch()) {
      return new SimonSays(this.totalSequence.addRandomColor(new Random()), true, null).addSpaces();
    }
    else {
      return this;
    }
  }
  
  // changes the color of

  // adds spaces to list flashing colors
  public SimonSays addSpaces() {
    return new SimonSays(this.totalSequence, this.remainingSequence.addSpacesHelp(),
        this.isGivingSequence, this.selectedColor);
  }

  // changes world state based on mouse clicks
  public SimonSays onMouseClicked(Posn p) {
    if (p.x < 250 && p.y < 250) {
      return new SimonSays(this.totalSequence, this.remainingSequence,
          this.isGivingSequence, Color.RED);
    }
    else if (p.x < 500 && p.x > 250 && p.y < 250) {
      return new SimonSays(this.totalSequence, this.remainingSequence,
          this.isGivingSequence, Color.GREEN);
    }
    else if (p.x < 250 && p.y > 250 && p.y < 500) {
      return new SimonSays(this.totalSequence, this.remainingSequence,
          this.isGivingSequence, Color.BLUE);
    }
    else if (p.x < 500 && p.x > 250 && p.y > 250 && p.y < 500) {
      return new SimonSays(this.totalSequence, this.remainingSequence,
          this.isGivingSequence, Color.ORANGE);
    }
    else {
      return this;
    }
  }

  // processes data inserted by mouse click
  public SimonSays processClick() {
    if (this.remainingSequence.matchesGivenColor(this.selectedColor)) {
      return new SimonSays(this.totalSequence, this.remainingSequence.runSequence(),
          this.isGivingSequence, null);
    }
    else {
      return this;
    }
  }

  // determines when the player loses
  public WorldEnd worldEnds() {
    if (this.isGivingSequence) {
      return new WorldEnd(false, makeScene());
    }
    if (this.selectedColor == null) {
      return new WorldEnd(false, makeScene());
    }
    else if (!this.remainingSequence.matchesGivenColor(this.selectedColor)) {
      return new WorldEnd(true, makeAFinalScene());
    }
    else {
      return new WorldEnd(false, makeScene());
    }
  }

  // makes final scene for when the player loses
  public WorldScene makeAFinalScene() {
    return new WorldScene(500, 500)
        .placeImageXY(new TextImage("You Lose", 50, Color.BLACK), 250, 250);
  }
}

// interface of list of circles 
interface ILoColor {

  // determines if the list of the remaining sequence matches the given color
  boolean matchesGivenColor(Color c);

  // runs through list on tick
  ILoColor runSequence();

  // determines if Simon Says is ready to switch modes
  boolean readyToSwitch();

  //returns a random color either blue, green, red, or orange 
  Color randomColor(Random r);

  // adds random color to the end of the list
  ILoColor addRandomColor(Random r);

  // adds spaces to list flashing colors
  ILoColor addSpacesHelp();
} 

// represent empty list of circles
class MtLoColor implements ILoColor {

  /*
   * TEMPLATE 
   * -------- 
   * Fields: N/A
   * Methods
   * ... this.matchesGivenColor(Color c) ...           -- boolean
   * ... this.runSequence() ...                        -- ILoColor
   * ... this.readToSwitch() ...                       -- boolean
   * ... this.randomColor(Random r) ...                -- Color
   * ... this.addRandomColor() ...                     -- ILoColor
   * ... this.addSpacesHelp() ...                      -- ILoColor
   * Methods on Fields: N/A
   */

  // determines if the list of the remaining sequence matches the given color
  public boolean matchesGivenColor(Color c) {
    return false;
  }

  // runs through list on tick
  public ILoColor runSequence() {
    return this;
  }

  // determines if Simon Says is ready to switch modes
  public boolean readyToSwitch() {
    return true;
  }

  //returns a random color either blue, green, red, or orange 
  public Color randomColor(Random r) {
    if (r.nextInt(4) == 0) {
      return Color.RED;
    }
    else if (r.nextInt(4) == 1) {
      return Color.GREEN;
    }
    else if (r.nextInt(4) == 2) {
      return Color.BLUE;
    }
    else {
      return Color.ORANGE;
    }
  }

  // adds random color to the end of the list 
  public ILoColor addRandomColor(Random r) {
    return new ConsLoColor(this.randomColor(r), new MtLoColor());
  }

  // adds spaces to list flashing colors
  public ILoColor addSpacesHelp() {
    return this;
  }
}

// represent a list of circles
class ConsLoColor implements ILoColor {
  Color first;
  ILoColor rest;
  Random rand;

  // default constructor
  ConsLoColor(Color first, ILoColor rest) {
    this.first = first;
    this.rest = rest;
  }

  // constructor to test for random values 
  ConsLoColor(Color first, ILoColor rest, Random rand) {
    this.first = first;
    this.rest = rest;
    this.rand = rand;
  }

  /*
   * TEMPLATE 
   * -------- 
   * Fields 
   * ... this.first ...                                -- int
   * ... this.rest ...                                 -- ILoInt
   * ... this.rand ...                                 -- Random
   * Methods
   * ... this.matchesGivenColor(Color c) ...           -- boolean
   * ... this.runSequence() ...                        -- ILoColor
   * ... this.readToSwitch() ...                       -- boolean
   * ... this.randomColor(Random r) ...                -- Color
   * ... this.addRandomColor() ...                     -- ILoColor
   * ... this.addSpacesHelp() ...                      -- ILoColor
   * Methods on Fields
   * ... this.rest.equals(c) ...                       -- boolean 
   * ... this.rest.addRandomColor(r) ...               -- ILoColor
   * ... this.rest.addSpacesHelp() ...                 -- ILoColor
   */

  // determines if the list of the remaining sequence matches the given color
  public boolean matchesGivenColor(Color c) {
    return this.first.equals(c);
  }

  // runs through list on tick
  public ILoColor runSequence() {
    return this.rest;
  }

  // determines if Simon Says is ready to switch modes
  public boolean readyToSwitch() {
    return false;
  }

  //returns a random color either blue, green, red, or orange
  public Color randomColor(Random r) {
    if (r.nextInt(4) == 0) {
      return Color.RED;
    }
    else if (r.nextInt(4) == 1) {
      return Color.GREEN;
    }
    else if (r.nextInt(4) == 2) {
      return Color.BLUE;
    }
    else {
      return Color.ORANGE;
    }
  }

  //adds random color to the end of the list
  public ILoColor addRandomColor(Random r) {
    return new ConsLoColor(this.first, this.rest.addRandomColor(r));
  }

  // adds spaces to list of flashing colors
  public ILoColor addSpacesHelp() {
    return new ConsLoColor(Color.WHITE,
        new ConsLoColor(this.first, this.rest.addSpacesHelp()));
  }
}

class ExamplesSimonSays {

  CircleImage circle1 = new CircleImage(50, OutlineMode.SOLID, Color.GREEN);
  CircleImage circle2 = new CircleImage(50, OutlineMode.SOLID, Color.BLUE);

  ILoColor empty = new MtLoColor();
  ILoColor list1 = new ConsLoColor(Color.BLUE, new ConsLoColor(Color.GREEN,
      new ConsLoColor(Color.RED, new ConsLoColor(Color.ORANGE, this.empty))));
  ILoColor list2 = new ConsLoColor(Color.GREEN, new ConsLoColor(Color.RED, 
      new ConsLoColor(Color.ORANGE, this.empty)));
  ILoColor list2W = new ConsLoColor(Color.WHITE, new ConsLoColor(Color.GREEN, 
      new ConsLoColor(Color.WHITE, new ConsLoColor(Color.RED, new ConsLoColor(Color.WHITE,
          new ConsLoColor(Color.ORANGE, this.empty))))));
  ILoColor listRest = new ConsLoColor(Color.RED, new ConsLoColor(Color.ORANGE, this.empty));
  ILoColor list3 = new ConsLoColor(Color.BLUE, new ConsLoColor(Color.GREEN, 
      new MtLoColor()), new Random(1));

  SimonSays simon1 = new SimonSays(this.empty, this.empty, true, null);
  SimonSays simon2 = new SimonSays(this.list1, this.list2, true, null);
  SimonSays simon3 = new SimonSays(this.list1, this.list2, false, Color.RED);
  SimonSays simon4 = new SimonSays(this.list1, this.empty, false, null);
  SimonSays simon5 = new SimonSays(this.list1, this.list2, false, Color.GREEN);

  // test for makeScene()
  boolean testMakeScene(Tester t) {
    return 
        t.checkExpect(simon1.makeScene(), new WorldScene(500, 500)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.RED)), 125, 125)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.GREEN)), 375, 125)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.BLUE)), 125, 375)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.ORANGE)), 375, 375))
        && t.checkExpect(simon2.makeScene(), new WorldScene(500, 500)
            .placeImageXY(simon2.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.RED)), 125, 125)
            .placeImageXY(simon2.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.GREEN.darker().darker())), 375, 125)
            .placeImageXY(simon2.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.BLUE)), 125, 375)
            .placeImageXY(simon2.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.ORANGE)), 375, 375));
  }

  // test for colorChange(CircleImage img)
  boolean testColorChange(Tester t) {
    return t.checkExpect(simon1.colorChange(circle1), 
        new CircleImage(50, OutlineMode.SOLID, Color.GREEN))
        && t.checkExpect(simon2.colorChange(circle1),
            new CircleImage(50, OutlineMode.SOLID, Color.GREEN.darker().darker()));
  }

  // test for onTick()
  boolean testOnTick(Tester t) {
    return t.checkExpect(simon1.onTick(), new SimonSays(this.empty, this.empty, false, null))
        && t.checkExpect(simon2.onTick(), new SimonSays(this.list1, new ConsLoColor(Color.RED, 
            new ConsLoColor(Color.ORANGE, this.empty)), true, null))
        && t.checkExpect(simon3.onTick(), new SimonSays(this.list1, this.list2, false, Color.RED));
  }

  // test for addSpaces()
  boolean testAddSpaces(Tester t) {
    return t.checkExpect(simon1.addSpaces(), new SimonSays(this.empty, this.empty, true, null))
        && t.checkExpect(simon2.addSpaces(), new SimonSays(this.list1, this.list2W, true, null));
  }

  // test for onMouseClicked() 
  boolean testOnMouseClicked(Tester t) {
    return t.checkExpect(simon1.onMouseClicked(new Posn(100, 100)), 
        new SimonSays(this.empty, this.empty, true, Color.RED))
        && t.checkExpect(simon2.onMouseClicked(new Posn(300, 150)), 
            new SimonSays(this.list1, this.list2, true, Color.GREEN))
        && t.checkExpect(simon3.onMouseClicked(new Posn(100, 300)), 
            new SimonSays(this.list1, this.list2, false, Color.BLUE))
        && t.checkExpect(simon4.onMouseClicked(new Posn(300, 300)), 
            new SimonSays(this.list1, this.empty, false, Color.ORANGE))
        && t.checkExpect(simon1.onMouseClicked(new Posn(600, 600)), this.simon1);
  }

  // test for processClick()
  boolean testProcessClick(Tester t) {
    return t.checkExpect(simon1.processClick(), this.simon1)
        && t.checkExpect(simon3.processClick(), this.simon3)
        && t.checkExpect(simon5.processClick(), 
            new SimonSays(this.list1, this.listRest, false, null));
  }

  // test for worldEnds()
  boolean testWorldEnds(Tester t) {
    return 
        t.checkExpect(simon1.worldEnds(), new WorldEnd(false, new WorldScene(500, 500)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.RED)), 125, 125)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.GREEN)), 375, 125)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.BLUE)), 125, 375)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.ORANGE)), 375, 375)))
        && t.checkExpect(simon4.worldEnds(), new WorldEnd(false, new WorldScene(500, 500)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.RED)), 125, 125)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.GREEN)), 375, 125)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.BLUE)), 125, 375)
            .placeImageXY(simon1.colorChange(
                new CircleImage(50, OutlineMode.SOLID, Color.ORANGE)), 375, 375)))
        && t.checkExpect(simon3.worldEnds(), new WorldEnd(true, new WorldScene(500, 500)
            .placeImageXY(new TextImage("You Lose", 50, Color.BLACK), 250, 250)));
  }

  // test for makeAFinalScene()
  boolean testMakeAFinalScene(Tester t) {
    return 
        t.checkExpect(simon3.makeAFinalScene(), new WorldScene(500, 500)
            .placeImageXY(new TextImage("You Lose", 50, Color.BLACK), 250, 250))
        && t.checkExpect(simon4.makeAFinalScene(), new WorldScene(500, 500)
            .placeImageXY(new TextImage("You Lose", 50, Color.BLACK), 250, 250));
  }

  // test for matchesGivenColor(Color c)
  boolean testMatchesGivenColor(Tester t) {
    return t.checkExpect(empty.matchesGivenColor(Color.GREEN), false) 
        && t.checkExpect(list1.matchesGivenColor(Color.BLUE), true)
        && t.checkExpect(list1.matchesGivenColor(Color.GREEN), false)
        && t.checkExpect(list2.matchesGivenColor(Color.GREEN), true);
  }

  // test for runSequence()
  boolean testRunSequence(Tester t) {
    return t.checkExpect(empty.runSequence(), this.empty)
        && t.checkExpect(list1.runSequence(), new ConsLoColor(Color.GREEN,
            new ConsLoColor(Color.RED, new ConsLoColor(Color.ORANGE, this.empty))));
  } 

  // test for readyToSwitch()
  boolean testReadyToSwitch(Tester t) {
    return t.checkExpect(empty.readyToSwitch(), true)
        && t.checkExpect(list1.readyToSwitch(), false)
        && t.checkExpect(list2.readyToSwitch(), false)
        && t.checkExpect(list3.readyToSwitch(), false);
  }

  // test for randomColor(Random r)
  boolean testRandomColor(Tester t) {
    return t.checkExpect(list3.randomColor(new Random(1)), Color.ORANGE)
        && t.checkExpect(list2.randomColor(new Random(2)), Color.GREEN)
        && t.checkExpect(list1.randomColor(new Random(3)), Color.ORANGE)
        && t.checkExpect(empty.randomColor(new Random(5)), Color.ORANGE);
  }

  // test for addRandomColor()
  boolean testAddRandomColor(Tester t) {
    return 
        t.checkExpect(empty.addRandomColor(new Random(1)), 
            new ConsLoColor(Color.ORANGE, new MtLoColor()))
        && t.checkExpect(list1.addRandomColor(new Random(2)), new ConsLoColor(Color.BLUE,
            new ConsLoColor(Color.GREEN, new ConsLoColor(Color.RED, 
                new ConsLoColor(Color.ORANGE, new ConsLoColor(list1.randomColor(new Random(2)), 
                    (new MtLoColor())))))));
  }

  // test for addSpacesHelp()
  boolean testAddSpacesHelp(Tester t) {
    return t.checkExpect(empty.addSpacesHelp(), this.empty)
        && t.checkExpect(list1.addSpacesHelp(), new ConsLoColor(Color.WHITE,
            new ConsLoColor(Color.BLUE, this.list2.addSpacesHelp())));
  }

  // runs the Simon Says game
  boolean testBigBang(Tester t) {
    SimonSays world = new SimonSays(new MtLoColor(),
        new MtLoColor(), true, null);
    int worldWidth = 500;
    int worldHeight = 500;
    double tickRate = .5;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }
}

