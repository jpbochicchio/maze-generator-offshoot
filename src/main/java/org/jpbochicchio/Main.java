package org.jpbochicchio;

import com.beust.jcommander.JCommander;
import de.amr.demos.maze.swingapp.model.Algorithm;
import de.amr.demos.maze.swingapp.model.MazeDemoModel;
import de.amr.demos.maze.swingapp.ui.control.ControlUI;
import de.amr.demos.maze.swingapp.ui.control.ControlView;
import de.amr.demos.maze.swingapp.ui.control.action.AfterGenerationAction;
import de.amr.demos.maze.swingapp.ui.grid.GridUI;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.maze.alg.traversal.RecursiveDFS;
import de.amr.swing.Swing;
import org.jpbochicchio.actions.CreateAndSaveMassMazes;
import org.jpbochicchio.config.ExtendedSettings;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Optional;

public class Main {
    public static int mazeGenerationCount = 0;

    public static void main(String[] args) {
        ExtendedSettings extendedSettings = new ExtendedSettings();
        JCommander commandLineProcessor = JCommander.newBuilder().addObject(extendedSettings).build();

        commandLineProcessor.usage();
        commandLineProcessor.parse(args);

        try {
            UIManager.setLookAndFeel(extendedSettings.theme);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                start(extendedSettings);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void start(ExtendedSettings settings) throws NoSuchFieldException, IllegalAccessException {
        MazeDemoModel model = new MazeDemoModel();
        model.createGrid(settings.width / model.getGridCellSize(), settings.height / model.getGridCellSize(), false, TraversalState.UNVISITED);

        GridUI gridUI = new GridUI(model, 336, 384);
        ControlUI controlUI = new ControlUI(gridUI, model);

        controlUI.setBusy(false);

        controlUI.setHiddenWhenBusy(false);
        controlUI.setAfterGenerationAction(AfterGenerationAction.IDLE);
        controlUI.expandWindow();

        Optional<Algorithm> generatorAlgo = model.findGenerator(RecursiveDFS.class);
        generatorAlgo.ifPresent(controlUI::selectGenerator);

        Optional<Algorithm> solver = model.findSolver(AStarSearch.class);
        solver.ifPresent(controlUI::selectSolver);

        System.out.println("Injecting custom action");
        CreateAndSaveMassMazes massGenerationAction = new CreateAndSaveMassMazes("Generate All Mazes", controlUI, gridUI);
        massGenerationAction.setMazeCount(settings.massGenerationMazeCount);

        Field controlViewField = controlUI.getClass().getDeclaredField("view");
        controlViewField.setAccessible(true);

        ControlView controlViewInstance = (ControlView) controlViewField.get(controlUI);
        controlViewInstance.getBtnCreateAllMazes().setAction(massGenerationAction);

        model.changePublisher.addPropertyChangeListener(controlUI);
        gridUI.setEscapeAction(Swing.action("Escape", (e) -> {
            controlUI.show();
        }));

        gridUI.startModelChangeListening();
        gridUI.show();

        controlUI.placeWindowRelativeTo(gridUI.getWindow());
        controlUI.show();
    }
}