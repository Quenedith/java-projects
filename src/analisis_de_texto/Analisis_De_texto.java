package analisis_de_texto;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Analisis_De_texto extends JFrame {
    // Componentes de la UI
    private JTextArea inputArea;
    private JTextArea translationArea;
    private JLabel lblLength, lblWordCount, lblFirstChar, lblLastChar,
                   lblMiddleChar, lblFirstWord, lblMiddleWord, lblLastWord;
    private JLabel lblCountA, lblCountE, lblCountI, lblCountO, lblCountU;
    private JLabel lblEvenWords, lblOddWords;
    private File currentFile;
    private boolean textFromFile = false;
    private final JFileChooser fileChooser = new JFileChooser();

    // Mapa para la "clave murciélago"
    private static final Map<Character,Character> MURCIELAGO = new HashMap<>();
    static {
        MURCIELAGO.put('m','0');
        MURCIELAGO.put('u','1');
        MURCIELAGO.put('r','2');
        MURCIELAGO.put('c','3');
        MURCIELAGO.put('i','4');
        MURCIELAGO.put('e','5');
        MURCIELAGO.put('l','6');
        MURCIELAGO.put('a','7');
        MURCIELAGO.put('g','8');
        MURCIELAGO.put('o','9');
    }

    public Analisis_De_texto() {
        super("PROGRAMACION II  -  MANEJO DE CADENAS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5,5));

        // --- Menú ---
        JMenuBar mb = new JMenuBar();

        // Archivo
        JMenu mArchivo = new JMenu("Archivo");
        JMenuItem miAbrir = new JMenuItem("Abrir");
        miAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        miAbrir.addActionListener(e -> openFile());
        JMenuItem miGuardar = new JMenuItem("Guardar");
        miGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        miGuardar.addActionListener(e -> saveFile());
        JMenuItem miGuardarComo = new JMenuItem("Guardar como");
        miGuardarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        miGuardarComo.addActionListener(e -> saveAsFile());
        mArchivo.add(miAbrir);
        mArchivo.add(miGuardar);
        mArchivo.add(miGuardarComo);
        mb.add(mArchivo);

        // Editar
        JMenu mEditar = new JMenu("Editar");
        JMenuItem miCopiar = new JMenuItem("Copiar");
        miCopiar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        miCopiar.addActionListener(e -> inputArea.copy());
        JMenuItem miCortar = new JMenuItem("Cortar");
        miCortar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        miCortar.addActionListener(e -> inputArea.cut());
        JMenuItem miPegar = new JMenuItem("Pegar");
        miPegar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        miPegar.addActionListener(e -> inputArea.paste());
        JMenuItem miBuscar = new JMenuItem("Buscar");
        miBuscar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        miBuscar.addActionListener(e -> findText());
        JMenuItem miReemplazar = new JMenuItem("Reemplazar");
        miReemplazar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        miReemplazar.addActionListener(e -> replaceText());
        mEditar.add(miCopiar);
        mEditar.add(miCortar);
        mEditar.add(miPegar);
        mEditar.add(miBuscar);
        mEditar.add(miReemplazar);
        mb.add(mEditar);

        setJMenuBar(mb);

        // --- Área de entrada + botón Procesar ---
        JPanel center = new JPanel(new BorderLayout(3,3));
        center.add(new JLabel("Ingrese un texto o abra un archivo:"), BorderLayout.NORTH);
        inputArea = new JTextArea(8, 60);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        center.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        JButton btnProc = new JButton("Procesar");
        btnProc.addActionListener(e -> processText());
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBtn.add(btnProc);
        center.add(pBtn, BorderLayout.SOUTH);

        add(center, BorderLayout.NORTH);

        // --- Panel de estadísticas ---
        JPanel stats = new JPanel(new GridLayout(8,4,5,5));
        stats.add(new JLabel("Longitud del texto:"));    lblLength     = new JLabel(); stats.add(lblLength);
        stats.add(new JLabel("Repeticiones de \"A\", \"a\" ó \"á\":")); lblCountA = new JLabel(); stats.add(lblCountA);
        stats.add(new JLabel("Total de palabras:"));     lblWordCount = new JLabel(); stats.add(lblWordCount);
        stats.add(new JLabel("Repeticiones de \"E\", \"e\" ó \"é\":")); lblCountE = new JLabel(); stats.add(lblCountE);
        stats.add(new JLabel("Primer letra del texto:")); lblFirstChar  = new JLabel(); stats.add(lblFirstChar);
        stats.add(new JLabel("Repeticiones de \"I\", \"i\" ó \"í\":")); lblCountI = new JLabel(); stats.add(lblCountI);
        stats.add(new JLabel("Última letra del texto:")); lblLastChar   = new JLabel(); stats.add(lblLastChar);
        stats.add(new JLabel("Repeticiones de \"O\", \"o\" ó \"ó\":")); lblCountO = new JLabel(); stats.add(lblCountO);
        stats.add(new JLabel("Letra central del texto:"));lblMiddleChar = new JLabel(); stats.add(lblMiddleChar);
        stats.add(new JLabel("Repeticiones de \"U\", \"u\" ó \"ú\":")); lblCountU = new JLabel(); stats.add(lblCountU);
        stats.add(new JLabel("Primera palabra:"));       lblFirstWord = new JLabel(); stats.add(lblFirstWord);
        stats.add(new JLabel("Palabras con cantidad de caracteres par:")); lblEvenWords = new JLabel(); stats.add(lblEvenWords);
        stats.add(new JLabel("Palabra central:"));       lblMiddleWord = new JLabel(); stats.add(lblMiddleWord);
        stats.add(new JLabel("Palabras con cantidad de caracteres impar:")); lblOddWords = new JLabel(); stats.add(lblOddWords);
        stats.add(new JLabel("Última palabra:"));        lblLastWord  = new JLabel(); stats.add(lblLastWord);
        stats.add(new JLabel()); stats.add(new JLabel());  // hueco vacío

        add(stats, BorderLayout.CENTER);

        // --- Traducción Murciélago ---
        JPanel south = new JPanel(new BorderLayout(3,3));
        south.add(new JLabel("TRADUCCIÓN A CLAVE MURCIÉLAGO:"), BorderLayout.NORTH);
        translationArea = new JTextArea(4, 60);
        translationArea.setEditable(false);
        translationArea.setLineWrap(true);
        translationArea.setWrapStyleWord(true);
        south.add(new JScrollPane(translationArea), BorderLayout.CENTER);

        add(south, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Métodos de archivo ---
    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                inputArea.setText(Files.readString(currentFile.toPath()));
                textFromFile = true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer archivo.");
            }
        }
    }

    private void saveFile() {
        if (textFromFile && currentFile != null) {
            try {
                Files.writeString(currentFile.toPath(), inputArea.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar.");
            }
        } else {
            saveAsFile();
        }
    }

    private void saveAsFile() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                Files.writeString(currentFile.toPath(), inputArea.getText());
                textFromFile = true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar.");
            }
        }
    }

    // --- Buscar / Reemplazar ---
    private void findText() {
        String term = JOptionPane.showInputDialog(this, "Buscar palabra:");
        if (term != null && !term.isEmpty()) {
            int idx = inputArea.getText().indexOf(term);
            if (idx >= 0) {
                inputArea.requestFocus();
                inputArea.select(idx, idx + term.length());
            } else {
                JOptionPane.showMessageDialog(this, "No encontrado.");
            }
        }
    }

    private void replaceText() {
        String find = JOptionPane.showInputDialog(this, "Palabra a reemplazar:");
        if (find != null) {
            String replace = JOptionPane.showInputDialog(this, "Reemplazar por:");
            if (replace != null) {
                inputArea.setText(inputArea.getText().replace(find, replace));
            }
        }
    }

    // --- Procesamiento de texto ---
    private void processText() {
        String txt = inputArea.getText();
        lblLength.setText(String.valueOf(txt.length()));

        String[] words = txt.trim().isEmpty() ? new String[0] : txt.trim().split("\\s+");
        lblWordCount.setText(String.valueOf(words.length));

        lblFirstChar.setText(txt.isEmpty() ? "-" : String.valueOf(txt.charAt(0)));
        lblLastChar .setText(txt.isEmpty() ? "-" : String.valueOf(txt.charAt(txt.length()-1)));
        lblMiddleChar.setText(txt.isEmpty() ? "-" : String.valueOf(txt.charAt(txt.length()/2)));

        lblFirstWord .setText(words.length>0 ? words[0] : "-");
        lblMiddleWord.setText(words.length>0 ? words[words.length/2] : "-");
        lblLastWord  .setText(words.length>0 ? words[words.length-1] : "-");

        int a=0,e=0,i=0,o=0,u=0;
        for (char c : txt.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                case 'a': case 'á': a++; break;
                case 'e': case 'é': e++; break;
                case 'i': case 'í': i++; break;
                case 'o': case 'ó': o++; break;
                case 'u': case 'ú': u++; break;
            }
        }
        lblCountA.setText(String.valueOf(a));
        lblCountE.setText(String.valueOf(e));
        lblCountI.setText(String.valueOf(i));
        lblCountO.setText(String.valueOf(o));
        lblCountU.setText(String.valueOf(u));

        int even=0, odd=0;
        for (String w : words) {
            if (w.length()%2==0) even++; else odd++;
        }
        lblEvenWords.setText(String.valueOf(even));
        lblOddWords .setText(String.valueOf(odd));

        StringBuilder sb = new StringBuilder();
        for (char c : txt.toCharArray()) {
            char lc = Character.toLowerCase(c);
            sb.append(MURCIELAGO.getOrDefault(lc, c));
        }
        translationArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Analisis_De_texto::new);
    }
}
 