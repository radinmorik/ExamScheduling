package no.hiof.examScheduling.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A custom JPanel that supports drag-and-drop functionality for files.
 * This panel allows users to drag Excel files onto the application.
 */
public class FileDragDropPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(FileDragDropPanel.class.getName());

    private final JLabel messageLabel;
    private final Border defaultBorder;
    private final Border dragBorder;
    private final Consumer<File> fileDroppedCallback;

    /**
     * Constructor that creates a drag-and-drop panel
     *
     * @param fileDroppedCallback Callback function that receives the dropped file
     */
    public FileDragDropPanel(Consumer<File> fileDroppedCallback) {
        this.fileDroppedCallback = fileDroppedCallback;

        // Set up the panel appearance
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 200));

        // Create borders for different states
        defaultBorder = BorderFactory.createDashedBorder(Color.GRAY, 2, 5, 5, true);
        dragBorder = BorderFactory.createDashedBorder(Color.BLUE, 2, 5, 5, true);
        setBorder(defaultBorder);

        // Create the message label
        messageLabel = new JLabel("Drag and drop Excel file here", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(messageLabel, BorderLayout.CENTER);

        // Set up drag and drop functionality
        setupDragAndDrop();
    }

    /**
     * Configures the drag and drop listeners for this panel
     */
    private void setupDragAndDrop() {
        // Create a new DropTarget and associate it with this panel
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                // Check if the dragged item is a file
                if (isExcelFileDrag(dtde)) {
                    setBorder(dragBorder);
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                setBorder(defaultBorder);
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();

                    @SuppressWarnings("unchecked")
                    List<File> droppedFiles = (List<File>) transferable.getTransferData(
                            DataFlavor.javaFileListFlavor);

                    if (!droppedFiles.isEmpty()) {
                        File file = droppedFiles.get(0);
                        if (isExcelFile(file)) {
                            // Notify the callback with the file
                            fileDroppedCallback.accept(file);
                            messageLabel.setText("File loaded: " + file.getName());
                        } else {
                            messageLabel.setText("Not an Excel file. Try again.");
                        }
                    }

                    dtde.dropComplete(true);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error processing dropped file", ex);
                    dtde.dropComplete(false);
                    messageLabel.setText("Error processing file. Try again.");
                } finally {
                    setBorder(defaultBorder);
                }
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                if (isExcelFileDrag(dtde)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }
        });
    }

    /**
     * Checks if the dragged item is an Excel file
     */
    private boolean isExcelFileDrag(DropTargetDragEvent dtde) {
        if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }

        try {
            Transferable transferable = dtde.getTransferable();

            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

            if (files.isEmpty()) {
                return false;
            }

            return isExcelFile(files.get(0));
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error checking dragged file", ex);
            return false;
        }
    }

    /**
     * Checks if a file is an Excel file by examining its extension
     *
     * @param file The file to check
     * @return true if the file has an Excel extension (.xls or .xlsx)
     */
    private boolean isExcelFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    /**
     * Updates the message displayed in the panel
     *
     * @param message The new message to display
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
