package com.miempresa.calculadora;  // ⬅️ elimina o adapta si no tienes paquete

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class Calculadora extends JFrame implements ActionListener, KeyListener {

    // 1. Campos
    private JTextField display;
    private String operador = "";
    private double operando1 = 0;
    private boolean inicioNumero = true;
    private static final String BITACORA = "bitacoraCalculadora.txt";

    // 2. Constructor
    public Calculadora() {
        super("Calculadora");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        // Display
        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        // Botones 4×4
        JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
        String[] botones = {
            "1","2","3","+",
            "4","5","6","-",
            "7","8","9","*",
            "0",".","=","/"
        };
        for (String t : botones) {
            JButton b = new JButton(t);
            b.setFont(new Font("Arial", Font.PLAIN, 20));
            b.addActionListener(this);
            panel.add(b);
        }
        add(panel, BorderLayout.CENTER);

        // Menú
        JMenuBar mb = new JMenuBar();
        JMenu mOpc = new JMenu("Opciones");
        JMenuItem miNuevo = new JMenuItem("Nuevo");
        JMenuItem miHist = new JMenuItem("Historial");
        miNuevo.addActionListener(e -> reiniciar());
        miHist.addActionListener(e -> mostrarHistorial());
        mOpc.add(miNuevo);
        mOpc.add(miHist);
        mb.add(mOpc);
        JMenu mAy = new JMenu("Ayuda");
        JMenuItem miMan = new JMenuItem("Manual de Usuario");
        miMan.addActionListener(e -> mostrarAyuda());
        mAy.add(miMan);
        mb.add(mAy);
        setJMenuBar(mb);

        // KeyListener + foco para recibir teclado físico y NumPad
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();

        setVisible(true);
    }

    // 3. Lógica de botones y teclas
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        Toolkit.getDefaultToolkit().beep();
        log(cmd);

        if ("0123456789.".contains(cmd)) {
            if (inicioNumero) display.setText(cmd);
            else display.setText(display.getText() + cmd);
            inicioNumero = false;

        } else if ("+-*/".contains(cmd)) {
            operando1 = Double.parseDouble(display.getText());
            operador = cmd;
            inicioNumero = true;

        } else if ("=".equals(cmd)) {
            double operando2 = Double.parseDouble(display.getText());
            double res = 0;
            switch (operador) {
                case "+": res = operando1 + operando2; break;
                case "-": res = operando1 - operando2; break;
                case "*": res = operando1 * operando2; break;
                case "/":
                    if (operando2 != 0) res = operando1 / operando2;
                    else JOptionPane.showMessageDialog(this, "División por cero");
                    break;
            }
            display.setText("" + res);
            inicioNumero = true;
        }
    }

    // 4. Reiniciar y log “Nuevo”
    private void reiniciar() {
        Toolkit.getDefaultToolkit().beep();
        display.setText("0");
        operador = "";
        inicioNumero = true;
        log("Nuevo");
    }

    // 5. Mostrar historial desde el archivo
    private void mostrarHistorial() {
        JFrame vh = new JFrame("Historial");
        JTextArea area = new JTextArea();
        area.setEditable(false);
        try {
            String texto = Files.readString(Paths.get(BITACORA));
            area.setText(texto);
        } catch (IOException ex) {
            area.setText("No hay historial.");
        }
        vh.add(new JScrollPane(area));
        vh.setSize(300, 400);
        vh.setLocationRelativeTo(this);
        vh.setVisible(true);
    }

    // 6. Diálogo de ayuda
    private void mostrarAyuda() {
        String msg =
            "Manual de Usuario de la Calculadora\n\n" +
            "- Usa el teclado numérico o haz clic en los botones.\n" +
            "- Cada pulsación genera un 'beep' y queda registrada.\n" +
            "- Menú Opciones > Nuevo: reinicia y registra 'Nuevo'.\n" +
            "- Menú Opciones > Historial: muestra todas las entradas.\n";
        JOptionPane.showMessageDialog(this, msg, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    // 7. Registrar en bitacoraCalculadora.txt
    private void log(String texto) {
        try (FileWriter fw = new FileWriter(BITACORA, true)) {
            fw.write(texto + System.lineSeparator());
        } catch (IOException ignored) {}
    }

    // 8. keyTyped para teclado estándar
    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        String vals = "0123456789.+-*/=\n";
        if (vals.indexOf(c) != -1) {
            String cmd = (c == '\n' ? "=" : String.valueOf(c));
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cmd));
        }
    }

    // 9. keyPressed para teclas NumPad y Enter
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // Números
        if (code >= KeyEvent.VK_NUMPAD0 && code <= KeyEvent.VK_NUMPAD9) {
            String cmd = String.valueOf(code - KeyEvent.VK_NUMPAD0);
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cmd));
        }
        // Operadores
        else if (code == KeyEvent.VK_ADD) {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "+"));
        } else if (code == KeyEvent.VK_SUBTRACT) {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "-"));
        } else if (code == KeyEvent.VK_MULTIPLY) {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "*"));
        } else if (code == KeyEvent.VK_DIVIDE) {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "/"));
        }
        // Enter como “=”
        else if (code == KeyEvent.VK_ENTER) {
            actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "="));
        }
    }
    @Override public void keyReleased(KeyEvent e) { }

    // 10. main
    public static void main(String[] args) {
        // asegurar existencia del archivo
        try {
            Files.createFile(Paths.get(BITACORA));
        } catch (IOException ignored) { }
        SwingUtilities.invokeLater(Calculadora::new);
    }
}
