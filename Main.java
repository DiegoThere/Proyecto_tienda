
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Marco_inicioSesion m1 = new Marco_inicioSesion();
        m1.setVisible(true);
        
    }
}

//login framework
class Marco_inicioSesion extends JFrame{
    public Marco_inicioSesion(){
        Toolkit miSistema = Toolkit.getDefaultToolkit();
        Dimension miPantalla = miSistema.getScreenSize();
        int anchura_pantalla = miPantalla.width;
        int altura_pantalla = miPantalla.height;

        setBounds(anchura_pantalla/4, altura_pantalla/4, anchura_pantalla/2, altura_pantalla/2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("Recursos\\Peru.png").getImage());
        setTitle("Inicio de sesion");
        Panel_inicioSesion p1 = new Panel_inicioSesion();
        setResizable(false);
        add(p1);

    }

    //The method does not recive data or return anything, it only serves to close the login
    public void cerrarInicio(){
        dispose();
    }


    class Panel_inicioSesion extends JPanel{
        public Panel_inicioSesion(){
            setLayout(new BorderLayout());

            //login panel top view
            //The panel is located in the northern area of the main panel, it has a Jlabel that contains text and a image 
            //text:name = "Arial", Style = PLAIN, Size = 50
            //Image: located on the left
            JPanel panel_superior = new JPanel();
            ImageIcon saludo = new ImageIcon("Recursos\\Saludo.png");
            JLabel titulo = new JLabel("Inicio de Sesion", saludo, SwingConstants.LEFT);
            titulo.setFont(new Font("Arial", Font.PLAIN, 50));
            panel_superior.add(titulo);
            add(panel_superior,BorderLayout.NORTH);

            //login panel Central view
            //The panel is located in the centern area of the main panel, it has username and password fields as well as a button to log in

            JPanel panel_central = new JPanel();
            panel_central.setLayout(null);
            panel_central.setPreferredSize(new Dimension(400,400));
            panel_central.setBackground(Color.GRAY);

            //The username part has two fields, a simple text with the username and a text field to enter the user
            JLabel texto_ususario = new JLabel("Usuario");
            texto_ususario.setFont(new Font("Arial", Font.PLAIN, 20));
            texto_ususario.setBounds(70, 100, 70, 30);
            panel_central.add(texto_ususario);

            campo_usuario = new JTextField(10);
            campo_usuario.setLocation(141, 105);
            campo_usuario.setSize(campo_usuario.getPreferredSize());
            panel_central.add(campo_usuario);
            add(panel_central);

            //The password zone has a JLabelthat contains the password name, and a JPasswordFiled where you can type the password
            JLabel texto_contraseña = new JLabel("Contraseña");
            texto_contraseña.setFont(new Font("Arial", Font.PLAIN, 20));
            texto_contraseña.setBounds(270, 100, 120, 30);
            panel_central.add(texto_contraseña);

            campo_contraseña = new JPasswordField(10);
            campo_contraseña.setLocation(375, 105);
            campo_contraseña.setSize(campo_contraseña.getPreferredSize());
            panel_central.add(campo_contraseña);
            
            //The login button
            JButton enviar = new JButton("Iniciar sesion");
            enviar.setLocation(520, 100);
            enviar.setSize(enviar.getPreferredSize());
            enviar.setBackground(new Color(173, 216, 250));
            enviar.addActionListener(new buscarUsuario());
            panel_central.add(enviar);

            add(panel_central);


        }

        private static final String URL =  "jdbc:mysql://localhost:3306/proyecto_tienda";
        private static final String PASSWORD = "peruKistan";
        private static final String USERNAME = "root";

        private JTextField campo_usuario;
        private JPasswordField campo_contraseña;


        public Connection getConexion(){
            Connection con = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
            } catch (Exception e) {
                System.out.println("Error al conectar a la base de datos MySQL: " + e.getMessage());
                e.printStackTrace();
            }
            return con;
        
        }

        private class buscarUsuario implements ActionListener{
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection con = null;
                ResultSet rs = null;
                PreparedStatement ps = null;
                
                try {
                    con = getConexion();
                    ps = con.prepareStatement("SELECT usuario_contraseña, categoria from usuarios where usuario_name = ?");
                    ps.setString(1, campo_usuario.getText());
                    rs = ps.executeQuery();
                    
                    
                    if(rs.next()){
                        String contraseña = rs.getString("usuario_contraseña");
                        String categoria = rs.getString("categoria");
                        char[] contraseñaB = campo_contraseña.getPassword();

                        ArrayList<String> permisos = new ArrayList<>();
                        permisos.add("Trabajador");
                        permisos.add("Administrador");
                        if(permisos.contains(categoria)){
                            if (contraseña.equals(String.copyValueOf(contraseñaB))) {
                                cerrarInicio();
                                Marco_productos m1 = new Marco_productos(campo_usuario.getText(),categoria);
                                m1.setVisible(true);
                            }else{
                                JOptionPane.showMessageDialog(null, "El usuario y/o contraseña son incorrectos","Error",JOptionPane.ERROR_MESSAGE);
                            }
                        }else{
                            JOptionPane.showMessageDialog(null, "No tienes permiso para ingresar","Error",JOptionPane.ERROR_MESSAGE);
                        }
                               
                    }else{
                        JOptionPane.showMessageDialog(null, "El usuario y/o contraseña son incorrectos","Error",JOptionPane.ERROR_MESSAGE);
                    }
    

                } catch (SQLException ex) {
                    
                    System.err.println(e);
                }finally{
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        System.err.println(ex);
                    }
                }
            }
            
        }
    }
}
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
class Marco_productos extends JFrame{
    public Marco_productos(String name, String puestoS){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit miSistema = Toolkit.getDefaultToolkit();
        Dimension miPantalla = miSistema.getScreenSize();
        int anchura_pantalla = miPantalla.width;
        int altura_pantalla = miPantalla.height;

        setBounds(anchura_pantalla/4, altura_pantalla/4, anchura_pantalla/2, altura_pantalla/2);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setIconImage(new ImageIcon("Recursos\\Peru.png").getImage());
        setIconImage(new ImageIcon("Recursos\\chems.png").getImage());
        setTitle("Gestion de Productos");
        Panel_productos p1 = new Panel_productos(name,puestoS);
        add(p1);
    }

    private class Panel_productos extends JPanel{

        JPanel panel_guardar;
        JTextField buscarLupa;
        JTextField buscarLupa2;

        JTextField campo_codigo,campo_nombre,campo_precio,campo_existencias;
        JTextArea campo_descripcion;
        JComboBox<String> campo_categoria;

        JPanel panel_Actualizar,panel_producto_actualizar;
        ArrayList<JTextArea> campos_actualizar = new ArrayList<>();

        ArrayList<JButton> botonCentral = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        JPanel panel_buscar;
        JPanel panel_buscar2;

        JTextField campo_codigo2,campo_nombre2,campo_precio2,campo_existencias2,campo_id;
        JTextArea campo_descripcion2;
        JComboBox<String> campo_categoria2;

        JPanel panel_superior;

        public Panel_productos(String name, String puestoS){
            setLayout(new BorderLayout());
            panel_superior = new JPanel();
            panel_superior.setLayout(new BorderLayout(30,0));
            panel_superior.setBackground(new Color(128,40,140));

            JLabel titulo = new JLabel("Sistema de Inventario Liverpool");
            titulo.setFont(new Font("Arial", Font.PLAIN, 50));
            titulo.setForeground(Color.BLACK);
            titulo.setHorizontalAlignment(SwingConstants.CENTER);
            panel_superior.add(titulo,BorderLayout.NORTH);
            
            

            JLabel usuario = new JLabel("Usuario: "+name);
            usuario.setFont(new Font("Arial", Font.PLAIN, 14));
            usuario.setForeground(Color.BLACK);
            panel_superior.add(usuario,BorderLayout.WEST);

            JLabel puesto = new JLabel("Puesto: "+puestoS);
            puesto.setFont(new Font("Arial", Font.PLAIN, 14));
            puesto.setForeground(Color.BLACK);
            panel_superior.add(puesto,BorderLayout.CENTER);

            ImageIcon lupa = new ImageIcon("Recursos\\Lupa.png");
            JButton boton_buscar = new JButton(lupa);
            boton_buscar.setBackground(Color.WHITE);
            boton_buscar.addActionListener(new activarActualizar());
            panel_buscar = new JPanel();
            buscarLupa = new JTextField(30);
            buscarLupa.setHorizontalAlignment(JTextField.LEFT);
            buscarLupa.setBackground(Color.WHITE);
            panel_buscar.add(boton_buscar);
            panel_buscar.add(buscarLupa);

            JButton boton_buscar2 = new JButton(lupa);
            boton_buscar2.setBackground(Color.WHITE);
            boton_buscar2.addActionListener(new activarActualizar2());
            panel_buscar2 = new JPanel();
            buscarLupa2 = new JTextField(30);
            buscarLupa2.setHorizontalAlignment(JTextField.LEFT);
            buscarLupa2.setBackground(Color.WHITE);
            panel_buscar2.add(boton_buscar2);
            panel_buscar2.add(buscarLupa2);
            
            
            

            add(panel_superior,BorderLayout.NORTH);



            JPanel panel_derecho = new JPanel();
            panel_derecho.setLayout(new GridLayout(2, 3));

            JButton guardar = new JButton("Registrar producto", new ImageIcon("Recursos\\Guardar.png"));
            guardar.setFont(new Font("Arial", Font.PLAIN, 20));
            guardar.setVerticalTextPosition(SwingConstants.TOP);
            guardar.setHorizontalTextPosition(SwingConstants.CENTER);
            guardar.addActionListener(new activarGuardar());
            guardar.setBackground(new Color(173, 216, 250));
            panel_derecho.add(guardar);

            JButton eliminar = new JButton("Eliminar producto", new ImageIcon("Recursos\\Eliminar.png"));
            eliminar.setFont(new Font("Arial", Font.PLAIN, 20));
            eliminar.setVerticalTextPosition(SwingConstants.TOP);
            eliminar.setHorizontalTextPosition(SwingConstants.CENTER);
            eliminar.addActionListener(new activarEliminar());
            eliminar.setBackground(new Color(173, 216, 250));
            panel_derecho.add(eliminar);

            JButton editar = new JButton("Editar producto", new ImageIcon("Recursos\\Editar.png"));
            editar.setFont(new Font("Arial", Font.PLAIN, 20));
            editar.setVerticalTextPosition(SwingConstants.TOP);
            editar.setHorizontalTextPosition(SwingConstants.CENTER);
            editar.addActionListener(new activarActualizar());
            editar.setBackground(new Color(173, 216, 250));
            panel_derecho.add(editar);

            JButton limpiar = new JButton("Limpiar pantalla", new ImageIcon("Recursos\\Limpiar.png"));
            limpiar.setFont(new Font("Arial", Font.PLAIN, 20));
            limpiar.setVerticalTextPosition(SwingConstants.TOP);
            limpiar.setHorizontalTextPosition(SwingConstants.CENTER);
            limpiar.addActionListener(new activarLimpiar());
            limpiar.setBackground(new Color(173, 216, 250));
            panel_derecho.add(limpiar);

            add(panel_derecho,BorderLayout.EAST);
            //-----------------------------------------------------------------------------------------------------------------------------------------------//
            panel_guardar = new JPanel();
            panel_guardar.setLayout(new GridBagLayout());

            JLabel texto_codigo = new JLabel("Codigo");
            campo_codigo = new JTextField(15);
            campo_codigo.getDocument().addDocumentListener(new Comprueba_codigo());

            JLabel texto_nombre = new JLabel("Nombre");
            campo_nombre = new JTextField(15);
            campo_nombre.getDocument().addDocumentListener(new Comprueba_nombre());

            JLabel texto_precio = new JLabel("Precio");
            campo_precio = new JTextField(8);
            campo_precio.getDocument().addDocumentListener(new Comprueba_precio());

            JLabel texto_existencias = new JLabel("Existencias");
            campo_existencias = new JTextField(10);
            campo_existencias.getDocument().addDocumentListener(new Comprueba_existencias());

            

            String[] categorias ={"Seleccionar","Alimentos","Bebidas","Limpieza","Cocina","Electrodomesticos","Ropa","Salud","Belleza","Mascotas"};
            JLabel texto_categoria = new JLabel("Categoria");
            campo_categoria = new JComboBox<>(categorias);

            JLabel texto_descripcion = new JLabel("Descripcion");
            campo_descripcion = new JTextArea(5, 15);
            campo_descripcion.setLineWrap(true);
            campo_descripcion.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane();
            scroll.setViewportView(campo_descripcion);
            campo_descripcion.getDocument().addDocumentListener(new Comprueba_descripcion());

            JButton boton_guardar = new JButton("Guardar");
            boton_guardar.addActionListener(new AccionGuardar());
            JButton boton_limpiar= new JButton("Limpiar");
            boton_limpiar.addActionListener(new AccionLimpiarGuardar());
            

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 10;
            gbc.gridy = 10;
            gbc.insets = new Insets(10,10,10,10);
            
            panel_guardar.add(texto_codigo,gbc);
            gbc.gridx++;
            panel_guardar.add(campo_codigo,gbc);
            gbc.gridy++;
            gbc.gridx--;
            panel_guardar.add(texto_nombre,gbc);
            gbc.gridx++;
            panel_guardar.add(campo_nombre,gbc);
            gbc.gridy++;
            gbc.gridx--;
            panel_guardar.add(texto_precio,gbc);
            gbc.gridx++;
            panel_guardar.add(campo_precio,gbc);
            gbc.gridy++;
            gbc.gridx--;
            panel_guardar.add(texto_existencias,gbc);
            gbc.gridx++;
            panel_guardar.add(campo_existencias,gbc);
            gbc.gridy++;
            gbc.gridx--;
            panel_guardar.add(texto_categoria,gbc);
            gbc.gridx++;
            panel_guardar.add(campo_categoria,gbc);
            gbc.gridy++;
            gbc.gridx--;
            panel_guardar.add(texto_descripcion,gbc);
            gbc.gridx++;
            panel_guardar.add(scroll,gbc);
            gbc.gridx--;
            gbc.gridy++;
            panel_guardar.add(boton_guardar,gbc);
            gbc.gridx++;
            panel_guardar.add(boton_limpiar,gbc);

            //----------------------------------------------------------------//

            panel_Actualizar = new JPanel();
            panel_Actualizar.setLayout(new FlowLayout());

            panel_producto_actualizar = new JPanel();
            panel_producto_actualizar.setLayout(new GridBagLayout());

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = 10;
            gbc2.gridy = 10;
            gbc2.insets = new Insets(10,10,10,10);

            JLabel texto_codigo2 = new JLabel("Codigo");
            campo_codigo2 = new JTextField(15);

            JLabel texto_nombre2 = new JLabel("Nombre");
            campo_nombre2 = new JTextField(15);

            JLabel texto_precio2 = new JLabel("Precio");
            campo_precio2 = new JTextField(8);


            JLabel texto_existencias2 = new JLabel("Existencias");
            campo_existencias2 = new JTextField(10);

            JLabel texto_categoria2 = new JLabel("Categoria");
            campo_categoria2 = new JComboBox<>(categorias);

            campo_id = new JTextField(5);
            campo_id.setVisible(false);

            JLabel texto_descripcion2 = new JLabel("Descripcion");
            campo_descripcion2 = new JTextArea(5, 15);
            campo_descripcion2.setLineWrap(true);
            campo_descripcion2.setWrapStyleWord(true);
            JScrollPane scroll2 = new JScrollPane();
            scroll2.setViewportView(campo_descripcion2);
            JButton boton_guardar2 = new JButton("Guardar");
            boton_guardar2.addActionListener(new BotonEditarProducto());
            JButton boton_cancelar= new JButton("Cancelar");
            boton_cancelar.addActionListener(new AccionCancelar());
            
            panel_producto_actualizar.add(texto_codigo2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(campo_codigo2,gbc2);
            gbc2.gridy++;
            gbc2.gridx--;
            panel_producto_actualizar.add(texto_nombre2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(campo_nombre2,gbc2);
            gbc2.gridy++;
            gbc2.gridx--;
            panel_producto_actualizar.add(texto_precio2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(campo_precio2,gbc2);
            gbc2.gridy++;
            gbc2.gridx--;
            panel_producto_actualizar.add(texto_existencias2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(campo_existencias2,gbc2);
            gbc2.gridy++;
            gbc2.gridx--;
            panel_producto_actualizar.add(texto_categoria2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(campo_categoria2,gbc2);
            gbc2.gridy++;
            gbc2.gridx--;
            panel_producto_actualizar.add(texto_descripcion2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(scroll2,gbc2);
            gbc2.gridx--;
            gbc2.gridy++;
            panel_producto_actualizar.add(boton_guardar2,gbc2);
            gbc2.gridx++;
            panel_producto_actualizar.add(boton_cancelar,gbc2);
            
            panel_producto_actualizar.add(campo_id,gbc2);

        }

        public class Comprueba_precio implements DocumentListener{

            @Override
            public void insertUpdate(DocumentEvent e) {
                comprobarPrecio(); 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                //comprobarPrecio();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }

            private void comprobarPrecio(){
                try {
                    double precio = Double.valueOf(campo_precio.getText());
                    
                    if (precio>=0.01&&precio<=99999999.99) {  

                    } else {
                        JOptionPane.showMessageDialog(null, "El precio tiene que estar entren 0.01 y 99,999,999.99");
                    }
                    
                } catch (NumberFormatException ex) {
                    
                    JOptionPane.showMessageDialog(null, "El precio tiene que ser un numero");
                }
            }

            
        }

        public class Comprueba_codigo implements DocumentListener{

            @Override
            public void insertUpdate(DocumentEvent e) {
                comprobarCodigo(); 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                comprobarCodigo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }

            private void comprobarCodigo(){
     
                String codigo = campo_codigo.getText();
                if (codigo.length()<=20) {
                    
                } else {
                    JOptionPane.showMessageDialog(null, "El codigo no puede estar vacio o superar 20 carcateres");
                }
                    
                
            }
            
        }

        public class Comprueba_nombre implements DocumentListener{

            @Override
            public void insertUpdate(DocumentEvent e) {
                comprobarNombre(); 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                comprobarNombre();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }

            private void comprobarNombre(){
     
                String nombre = campo_nombre.getText();
                if(nombre.length()<=45) {
                    
                } else {
                    JOptionPane.showMessageDialog(null, "El nombre no puede estar vacio o superar 45 carcateres");
                }
                    
                
            }
            
        }

        public class Comprueba_existencias implements DocumentListener{

            @Override
            public void insertUpdate(DocumentEvent e) {
                comprobarExistencias(); 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                //comprobarExistencias();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }

            private void comprobarExistencias(){
     
                try {
                    int existencias = Integer.valueOf(campo_existencias.getText());
                    if (existencias>0) {
                        
                    } else {
                        JOptionPane.showMessageDialog(null, "Las existencias no puede ser numeros negativos");
                    }
                    
                } catch (NumberFormatException ex) {
                    
                    JOptionPane.showMessageDialog(null, "Las existencias tiene que ser un numero positivo");
                }                    
                
            }
            
        }

        public class Comprueba_descripcion implements DocumentListener{

            @Override
            public void insertUpdate(DocumentEvent e) {
                comprobarDescripcion(); 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                comprobarDescripcion();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }

            private void comprobarDescripcion(){
     
                String codigo = campo_descripcion.getText();
                if (codigo.length()<=100) {
                    campo_descripcion.setBackground(SystemColor.window);
                } else {
                    campo_descripcion.setBackground(Color.RED);
                }
                    
                
            }
            
        }

        


        public class activarActualizar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand()=="Editar producto"){
                    panel_Actualizar.removeAll();
                    limpiar_panel_central();
                    buscar("Editar","SELECT * FROM productos");
                    panel_superior.add(panel_buscar,BorderLayout.EAST);
                    add(panel_Actualizar, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }else{
                    panel_Actualizar.removeAll();
                    limpiar_panel_central();

                    buscar("Editar","SELECT * FROM productos where Codigo_p = "+buscarLupa.getText());
                    buscarLupa.setText(null);
                    panel_superior.add(panel_buscar,BorderLayout.EAST);
                    add(panel_Actualizar, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
                
            }
            
        }

        public class activarActualizar2 implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand()=="Eliminar producto"){
                    panel_Actualizar.removeAll();
                    limpiar_panel_central();
                    buscarEliminar("Eliminar","SELECT * FROM productos");
                    panel_superior.add(panel_buscar2,BorderLayout.EAST);
                    add(panel_Actualizar, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }else{
                    panel_Actualizar.removeAll();
                    limpiar_panel_central();

                    buscarEliminar("Eliminar","SELECT * FROM productos where Codigo_p = "+buscarLupa2.getText());
                    buscarLupa2.setText(null);
                    panel_superior.add(panel_buscar2,BorderLayout.EAST);
                    add(panel_Actualizar, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
                
            }
            
        }

        public class activarEliminar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                panel_Actualizar.removeAll();
                limpiar_panel_central();
                buscarEliminar("Eliminar","SELECT * FROM productos");
                buscarLupa2.setText(null);
                panel_superior.add(panel_buscar2,BorderLayout.EAST);
                add(panel_Actualizar, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
            
        }

        public void limpiar_panel_central(){
            remove(panel_guardar);
            remove(panel_Actualizar);
            remove(panel_producto_actualizar);
            panel_superior.remove(panel_buscar);
            panel_superior.remove(panel_buscar2);
            limpiar_campos_guardar();
            revalidate();
            repaint();
        }

        public class activarGuardar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                
                limpiar_panel_central();
                add(panel_guardar, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
            
        }

        public class activarLimpiar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar_panel_central();
            }
            
        }

        private static final String URL =  "jdbc:mysql://localhost:3306/proyecto_tienda";
        private static final String PASSWORD = "peruKistan";
        private static final String USERNAME = "root";

        public Connection getConexion(){
            Connection con = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
            } catch (Exception e) {
                System.out.println("Error al conectar a la base de datos MySQL: " + e.getMessage());
                e.printStackTrace();
            }
            return con;
        
        }

        public class AccionLimpiarGuardar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar_campos_guardar();
            }

        }

        public void limpiar_campos_guardar(){
            campo_codigo.setText(null);
            campo_nombre.setText(null);
            campo_descripcion.setText(null);
            campo_precio.setText(null);
            campo_existencias.setText(null);
            campo_categoria.setSelectedIndex(0);
        }

        public class AccionGuardar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (campo_categoria.getSelectedItem().toString().equals("Seleccionar")) {
                    JOptionPane.showMessageDialog(null,"Seleccionar no es valido");
                }else if(campo_codigo.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"El codigo no puede ir vacio");
                }else if(campo_nombre.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"El nombre no puede ir vacio");
                }else if(campo_precio.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"El precio no puede ir vacio");
                }else if(campo_existencias.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Las existencias no pueden ir vacias");
                }else {
                    Connection con = null;
                    PreparedStatement ps = null;
                    try {
                        con = getConexion();
                        ps = con.prepareStatement("INSERT INTO productos(Codigo_p,Nombre_p,Descripcion_p,Precio_p,Existencias_p,Categoria_p) values(?,?,?,?,?,?)");
                        ps.setString(1,campo_codigo.getText());
                        ps.setString(2, campo_nombre.getText());
                        ps.setString(3, campo_descripcion.getText());
                        ps.setDouble(4,Double.parseDouble(campo_precio.getText()));
                        ps.setInt(5, Integer.parseInt(campo_existencias.getText()));
                        ps.setString(6, campo_categoria.getSelectedItem().toString());

                        int res = ps.executeUpdate();
                        limpiar_campos_guardar();
                        if(res>0){
                            JOptionPane.showMessageDialog(null, "Producto registrado","Exito",JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Datos del producto incorrectos","Error",JOptionPane.ERROR_MESSAGE);
                        }
                        con.close();
                    } catch (Exception ex) {
                        String codigo = campo_codigo.getText();
                        double precio = Double.valueOf(campo_precio.getText());
                        String nombre = campo_nombre.getText();
                        try {
                          int existencias = Integer.valueOf(campo_existencias.getText());   
                          if (existencias<0) {
                          }
                        } catch (Exception ee) {
                            System.err.println(ee); 
                            JOptionPane.showMessageDialog(null, "Las exitencias tiene un limite de 2,147,483,647 numeros enteneros\n");
                        }
                        
                        if(codigo.length()>20){
                            JOptionPane.showMessageDialog(null, "El codigo tiene "+(codigo.length()-20) +" caractere(s) de mas ");
                        }else if(precio<0.01||precio>99999999.99){
                            JOptionPane.showMessageDialog(null, "El precio tiene que estar entren 0.01 y 99,999,999.99");
                        }else if(nombre.length()>45){
                            JOptionPane.showMessageDialog(null, "El nombre tiene "+(nombre.length()-45)+" caractere(s) de mas ");
                        }
                        System.err.println(ex);  
                    }
                }
                
            }
            
        }

        public void buscar(String name_boton,String acccion){
            campos_actualizar.clear();
            botonCentral.clear();
            ids.clear();
                Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    con = getConexion();
                    ps = con.prepareStatement(acccion);
                    rs = ps.executeQuery();
                    int i =0;
                    while (rs.next()) {
                        String textoRecuperado ="";
                        String id;
                        id = rs.getString("idProductos");
                        textoRecuperado +=("Codigo: "+rs.getString("Codigo_p"));
                        textoRecuperado +=("\nNombre: "+rs.getString("Nombre_p"));
                        textoRecuperado +=("\nDescripcion: "+rs.getString("Descripcion_p"));
                        textoRecuperado +=("\nPrecio: "+rs.getString("Precio_p"));
                        textoRecuperado +=("\nExistencias: "+rs.getString("Existencias_p"));
                        textoRecuperado +=("\nCategoria: "+rs.getString("Categoria_p"));
                        campos_actualizar.add(new JTextArea(textoRecuperado));
                        campos_actualizar.get(i).setPreferredSize(new Dimension(200, 150));  
                        campos_actualizar.get(i).setLineWrap(true);
                        campos_actualizar.get(i).setEditable(false);
                        panel_Actualizar.add(campos_actualizar.get(i));
                        botonCentral.add(new JButton(name_boton));
                        ids.add(Integer.valueOf(id));
                        botonCentral.get(i).addActionListener(new ActivarEditarProducto(ids.get(i)));
                        panel_Actualizar.add(botonCentral.get(i));
                        i++;
                    }
                    con.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, acccion,"Error",JOptionPane.ERROR_MESSAGE);
                    System.err.println(ex); 
                    
                }
        } 

        
        public class ActivarEditarProducto implements ActionListener{
            int id;
            public ActivarEditarProducto(int id){
                this.id = id;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar_panel_central();
                
                add(panel_producto_actualizar, BorderLayout.CENTER);
                buscarUno(id);
                campo_id.setText(String.valueOf(id) );
                revalidate();
                repaint();
            }
            
        }

        public void buscarUno(int id){
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = getConexion();
                ps = con.prepareStatement("SELECT * FROM productos where idProductos = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();

                if (rs.next()) {
                   campo_codigo2.setText(rs.getString("Codigo_p"));
                   campo_nombre2.setText(rs.getString("Nombre_p"));
                   campo_descripcion2.setText(rs.getString("Descripcion_p"));
                   campo_precio2.setText(rs.getString("Precio_p"));
                   campo_existencias2.setText(rs.getString("Existencias_p"));
                   campo_categoria2.setSelectedItem(rs.getString("Categoria_p"));
                   
                }else{
                    JOptionPane.showMessageDialog(null, "No existe una persona con la clave");
                }
                con.close();
            }catch(Exception ex) {
                System.err.println(ex);
            }
        }

        

        public class BotonEditarProducto implements ActionListener{
    
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (campo_categoria2.getSelectedItem().toString().equals("Seleccionar")) {
                    JOptionPane.showMessageDialog(null,"Seleccionar no es valido");
                }else if(campo_codigo2.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"El codigo no puede ir vacio");
                }else if(campo_nombre2.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"El nombre no puede ir vacio");
                }else if(campo_precio2.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"El precio no puede ir vacio");
                }else if(campo_existencias2.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Las existencias no pueden ir vacias");
                }else {
                    Connection con = null;
                    PreparedStatement ps = null;
                    try {
                    
                    con = getConexion();
                    ps = con.prepareStatement("UPDATE productos set Codigo_p = ?,Nombre_p = ?,Descripcion_p = ?, Precio_p = ?,Existencias_p = ?, Categoria_p = ? WHERE idProductos = ?");
                    ps.setString(1, campo_codigo2.getText());
                    ps.setString(2, campo_nombre2.getText());
                    ps.setString(3, campo_descripcion2.getText());
                    ps.setDouble(4,Double.parseDouble(campo_precio2.getText()));
                    ps.setInt(5, Integer.parseInt(campo_existencias2.getText()));
                    ps.setString(6, campo_categoria2.getSelectedItem().toString());
                    ps.setInt(7,Integer.parseInt(campo_id.getText()));
                        
                    int res = ps.executeUpdate();
        
                    if (res>0) {
                        JOptionPane.showMessageDialog(null, "Producto Modificado");
                        panel_Actualizar.removeAll();
                        limpiar_panel_central();
                        buscar("Editar","SELECT * FROM productos");
                        panel_superior.add(panel_buscar,BorderLayout.EAST);
                        add(panel_Actualizar, BorderLayout.CENTER);
                        revalidate();
                        repaint();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al modificar");
                    }
                    con.close();
                    }catch(Exception ex) {
                        String codigo = campo_codigo2.getText();
                        double precio = Double.valueOf(campo_precio2.getText());
                        String nombre = campo_nombre2.getText();
                        try {
                          int existencias = Integer.valueOf(campo_existencias2.getText());   
                          if (existencias<0) {
                          }
                        } catch (Exception ee) {
                            System.err.println(ee); 
                            JOptionPane.showMessageDialog(null, "Las exitencias tiene un limite de 2,147,483,647 numeros enteneros\n");
                        }
                        
                        if(codigo.length()>20){
                            JOptionPane.showMessageDialog(null, "El codigo tiene "+(codigo.length()-20) +" caractere(s) de mas ");
                        }else if(precio<0.01||precio>99999999.99){
                            JOptionPane.showMessageDialog(null, "El precio tiene que estar entren 0.01 y 99,999,999.99");
                        }else if(nombre.length()>45){
                            JOptionPane.showMessageDialog(null, "El nombre tiene "+(nombre.length()-45)+" caractere(s) de mas ");
                        }
                        System.err.println(ex); 
                    }finally{
                        
                        
                    }
                }
            }

        }
        

        public class AccionCancelar implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar_panel_central();
                add(panel_Actualizar, BorderLayout.CENTER);
                panel_superior.add(panel_buscar,BorderLayout.EAST);
                revalidate();
                repaint();
            }
            
        }

        public void buscarEliminar(String name_boton,String accion){
            campos_actualizar.clear();
            botonCentral.clear();
            ids.clear();
                Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    con = getConexion();
                    ps = con.prepareStatement(accion);
                    rs = ps.executeQuery();
                    int i =0;
                    while (rs.next()) {
                        String textoRecuperado ="";
                        String id;
                        id = rs.getString("idProductos");
                        textoRecuperado +=("Codigo: "+rs.getString("Codigo_p"));
                        textoRecuperado +=("\nNombre: "+rs.getString("Nombre_p"));
                        textoRecuperado +=("\nDescripcion: "+rs.getString("Descripcion_p"));
                        textoRecuperado +=("\nPrecio: "+rs.getString("Precio_p"));
                        textoRecuperado +=("\nExistencias: "+rs.getString("Existencias_p"));
                        textoRecuperado +=("\nCategoria: "+rs.getString("Categoria_p"));
                        campos_actualizar.add(new JTextArea(textoRecuperado));
                        campos_actualizar.get(i).setPreferredSize(new Dimension(190, 150));  
                        campos_actualizar.get(i).setLineWrap(true);
                        campos_actualizar.get(i).setEditable(false);
                        panel_Actualizar.add(campos_actualizar.get(i));
                        botonCentral.add(new JButton(name_boton));
                        ids.add(Integer.valueOf(id));
                        botonCentral.get(i).addActionListener(new ActivarEliminarProducto(ids.get(i)));
                        panel_Actualizar.add(botonCentral.get(i));
                        i++;
                    }
                    con.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Datos del producto incorrectos","Error",JOptionPane.ERROR_MESSAGE);
                    System.err.println(ex); 
                    
                }
        } 

        public class ActivarEliminarProducto implements ActionListener{
            int id;
            public ActivarEliminarProducto(int id){
                this.id = id;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar_panel_central();
                eliminarUno(id);
                revalidate();
                repaint();
            }
            
        }

        public void eliminarUno(int id){
            Connection con = null;
            PreparedStatement ps = null;
            try {
                con = getConexion();
                ps = con.prepareStatement("DELETE FROM productos WHERE idproductos =?");
                ps.setInt(1, id);
                int res = ps.executeUpdate();

                if (res>0) {
                    JOptionPane.showMessageDialog(null, "Producto Eliminado");
                    
                   
                }else{
                    JOptionPane.showMessageDialog(null, "Error al eliminar");
                }
                con.close();
            }catch(Exception ex) {
                System.err.println(ex);
            }finally{
                panel_Actualizar.removeAll();
                limpiar_panel_central();
                buscarEliminar("Eliminar","SELECT * FROM productos");
                panel_superior.add(panel_buscar2,BorderLayout.EAST);
                add(panel_Actualizar, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        }

        
    }

    
}