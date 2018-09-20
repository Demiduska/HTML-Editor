package com.javarush.task.task32.task3209;

import com.javarush.task.task32.task3209.Controller;
import com.javarush.task.task32.task3209.listeners.FrameListener;
import com.javarush.task.task32.task3209.listeners.TabbedPaneChangeListener;
import com.javarush.task.task32.task3209.listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame implements ActionListener {
    private Controller controller;
    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextPane htmlTextPane = new JTextPane();
    private JEditorPane plainTextPane = new JEditorPane();



    public Controller getController() {
        return controller;
    }
    public View(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException e) {
            ExceptionHandler.log(e);
        } catch (InstantiationException e) {
            ExceptionHandler.log(e);
        } catch (UnsupportedLookAndFeelException e) {
            ExceptionHandler.log(e);
        } catch (ClassNotFoundException e) {
            ExceptionHandler.log(e);
        }
    }
    public void undo(){
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            ExceptionHandler.log(e);
        }
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            ExceptionHandler.log(e);
        }
    }
    public void  selectHtmlTab(){
        tabbedPane.setSelectedIndex(0);
        //Сбрасывать все правки с помощью метода
        resetUndo();
    }
    public void showAbout(){
        JOptionPane.showMessageDialog(this, "HTML Editor", "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }
    public void resetUndo(){
        undoManager.discardAllEdits();
    }
    //oн должен возвращать true, если выбрана вкладка, отображающая html в панели вкладок
    public boolean isHtmlTabSelected(){
        return tabbedPane.getSelectedIndex() == 0;
    }
    public void  update(){
        htmlTextPane.setDocument(controller.getDocument());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         String command =e.getActionCommand();
         switch (command) {
             case "Новый": {
                 controller.createNewDocument();
             }
             case "Открыть" : {
                 controller.openDocument();
             }
             case "Сохранить": {
                 controller.saveDocument();
             }
             case "Сохранить как...": {
                 controller.saveDocumentAs();
             }
             case "Выход": {
                 controller.exit();
             }
             case "О программе": {
                 showAbout();
             }
         }

    }
    public boolean canUndo(){
        return undoManager.canUndo();
    }

    public boolean canRedo(){
        return undoManager.canRedo();
    }
    public void init() {
        initGui();
        FrameListener frameListener = new FrameListener(this);
        addWindowListener(frameListener);
        setVisible(true);
    }
    public void exit() {
        controller.exit();
    }

    public void initMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        //С помощью MenuHelper инициализировать меню
        MenuHelper.initFileMenu(this, menuBar);
        MenuHelper.initEditMenu(this, menuBar);
        MenuHelper.initStyleMenu(this, menuBar);
        MenuHelper.initAlignMenu(this, menuBar);
        MenuHelper.initColorMenu(this, menuBar);
        MenuHelper.initFontMenu(this, menuBar);
        MenuHelper.initHelpMenu(this, menuBar);

        //Добавлять в верхнюю часть панели контента текущего фрейма нашу панель меню, аналогично тому, как это мы делали с панелью вкладок
        getContentPane().add(menuBar, BorderLayout.NORTH);

    }
    public void selectedTabChanged() {
    if(tabbedPane.getSelectedIndex()==0) {
        controller.setPlainText(plainTextPane.getText());
    }
    else {
        plainTextPane.setText(controller.getPlainText());
        }
        resetUndo();

    }

    public void initEditor(){
    htmlTextPane.setContentType("text/html");
    tabbedPane.add("Текст", new JScrollPane(plainTextPane));
    tabbedPane.add("HTML",new JScrollPane(htmlTextPane));
    tabbedPane.setPreferredSize(new Dimension(500,500));
    tabbedPane.addChangeListener(new TabbedPaneChangeListener(this));
    getContentPane().add(tabbedPane,BorderLayout.CENTER);
    }

    public void initGui(){
        initMenuBar();
        initEditor();
        pack();
    }
}
