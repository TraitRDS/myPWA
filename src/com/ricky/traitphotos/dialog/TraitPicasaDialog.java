package com.ricky.traitphotos.dialog;

import com.google.gdata.data.ExtensionProfile;
import com.google.gdata.data.OtherContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;
import com.ricky.google.photos.Application;
import com.ricky.google.photos.Client;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class TraitPicasaDialog extends JDialog {
    private Application application;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldUserName;
    private JButton buttonAuthorize;
    private JTree treeAlbums;
    private JTextField textFieldDirectory;
    private JButton buttonDirectory;
    private JButton createAlbumButton;

    private class AlbumTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (value instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode)value).getUserObject() instanceof AlbumEntry) {
                AlbumEntry albumEntry = (AlbumEntry) ((DefaultMutableTreeNode)value).getUserObject();
                return super.getTreeCellRendererComponent(tree, albumEntry.getTitle().getPlainText(), sel, expanded, leaf, row, hasFocus);
            }
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    public TraitPicasaDialog() {
        contentPane.setPreferredSize(new Dimension(800, 600));
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonAuthorize);
        treeAlbums.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        treeAlbums.setCellRenderer(new AlbumTreeRenderer());

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        buttonAuthorize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAuthorize();
            }
        });
        buttonDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });
        createAlbumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onCreateAlbum();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ServiceException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void onCreateAlbum() throws IOException, ServiceException {
        File albumDirectory = new File(textFieldDirectory.getText());
        application.addAlbum(albumDirectory);

    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textFieldDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onAuthorize() {
        try {
            application = new Application();
            application.authorize(textFieldUserName.getText());
            DefaultMutableTreeNode rootAlbumsNode = new DefaultMutableTreeNode("All Albums");
            java.util.List<AlbumEntry> albums = application.getAlbums();
            for (AlbumEntry albumEntry : albums) {
                System.out.println("Album " + albumEntry.getTitle().getPlainText());
                DefaultMutableTreeNode albumEntryNode = new DefaultMutableTreeNode(albumEntry);
                rootAlbumsNode.add(albumEntryNode);
            }

            treeAlbums.setModel(new DefaultTreeModel(rootAlbumsNode));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }


    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
