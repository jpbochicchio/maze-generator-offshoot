package org.jpbochicchio.actions;

import de.amr.demos.maze.swingapp.ui.control.ControlUI;
import de.amr.demos.maze.swingapp.ui.control.action.AfterGenerationAction;
import de.amr.demos.maze.swingapp.ui.control.action.CreateSingleMaze;
import de.amr.demos.maze.swingapp.ui.grid.GridUI;
import de.amr.graph.grid.ui.animation.GridCanvasAnimation;
import de.amr.util.StopWatch;
import org.jpbochicchio.Main;
import org.jpbochicchio.utilities.ImageCaptureUtility;

import java.awt.event.ActionEvent;
import java.util.Objects;

public class CreateAndSaveMassMazes extends CreateSingleMaze {
    private int mazeCount;

    public CreateAndSaveMassMazes(String name, ControlUI controlUI, GridUI gridUI) {
        super(name, controlUI, gridUI);
        this.mazeCount = 1;
    }

    public void setMazeCount(int mazeCount) {
        this.mazeCount = mazeCount;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.controlUI.startBackgroundThread(this::triggerMassMazeGeneration, (interruption) -> {
            this.controlUI.showMessage("Animation interrupted", new Object[0]);
            this.controlUI.reset();
        }, (failure) -> {
            failure.printStackTrace(System.err);
            this.controlUI.showMessage("Maze creation failed: %s", new Object[]{failure.getClass().getSimpleName()});
            this.controlUI.reset();
        });
    }

    public void triggerMassMazeGeneration() {
        this.controlUI.getSelectedGenerator().ifPresent((generatorInfo) -> {
            this.controlUI.startBackgroundThread(() -> {
                for (int i = 0; i < mazeCount; i++) {
                    System.out.printf("Starting job for maze %d%n", mazeCount);

                    this.model.emptyGrid();
                    this.gridUI.clear();
                    this.createMaze(generatorInfo, this.model.getGenerationStart());

                    AfterGenerationAction andNow = this.controlUI.getAfterGenerationAction();
                    if (andNow == AfterGenerationAction.FLOOD_FILL) {
                        GridCanvasAnimation.pause(1.0F);
                        StopWatch watch = new StopWatch();
                        GridUI gridInterface = this.gridUI;
                        Objects.requireNonNull(gridInterface);
                        watch.measure(gridInterface::floodFill);
                        this.controlUI.showMessage("Flood-fill: %.3f seconds.", new Object[]{watch.getSeconds()});
                    } else if (andNow == AfterGenerationAction.SOLVE) {
                        GridCanvasAnimation.pause(1.0F);
                        this.controlUI.runSelectedSolver();
                    }

                    System.out.println("Saving image");

                    String imageName = String.format("generatedMaze%d.png", Main.mazeGenerationCount);
                    ImageCaptureUtility.captureImage(this.gridUI.getWindow(), imageName);

                    System.out.printf("Saved maze %d to %s%n", Main.mazeGenerationCount, imageName);

                    Main.mazeGenerationCount += 1;
                }
            }, (interruption) -> {
                this.controlUI.showMessage("Animation interrupted", new Object[0]);
                this.controlUI.reset();
            }, (failure) -> {
                this.controlUI.showMessage("Maze generation failed: %s", new Object[]{failure.getClass().getSimpleName()});
                this.controlUI.reset();
            });
        });
    }
}
