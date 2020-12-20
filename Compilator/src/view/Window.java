package view;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import logic.Compilator;
import utils.ElementoTS;
import utils.FileUtils;

import javax.swing.event.CaretEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Window {

	private static String SEARCH_MSG = "Ingrese el nombre para su archivo.";
	private static String FILE_LOAD_ERROR_MSG = "Error al cargar el archivo.";
	private static String FILE_NOT_FOUND_MSG = "Archivo no encontrado.";
	private static String FILE_NOT_STORED = "El archivo debe ser cargado o guardado antes de compilar.";
	private static String SAVE_CORRECT_MSG = "Archivo guardado con éxito.";
	private static String SAVE_ERROR_MSG = "Error al guardar el archivo.";
	private static String LINE_COUNTER_MSG = "Línea seleccionada: ";
	private static String FILE_CREATED_MSG = "El archivo fue creado exitosamente.";
	private static String FILE_ALREADY_EXIST_MSG = "El archivo especificado ya existe.";
	private static String FILE_CREATE_ERROR_MSG = "Hubo algún error al crear el archivo.";
	private static String ASK_TO_MAKE_EXECUTABLE = "¿Desea crear el ejecutable además del archivo asm?";
	private static String SELECT_FOLDER = "Seleccione el archivo \"ml.exe\" de Masm32.";
	private static String INVALID_SELECTION = "Selección inválida";
	private static String INCORRECT_PATH = "El archivo seleccionado no es el necesario o la carpeta no contiene a el archivo \\\"link.exe\\\" que acompaña al indicado.";
	private static String CORRECT_COMPILATION = "La compilación fue un éxito.";
	private static String INCORRECT_COMPILATION = "Hubo un problema en la compilación.";
	private static String ASSEMBLER_GENERATED = "Ensamblador generado con éxito.";
	private static int END_OF_TEXT = 3;

	final UndoManager undo;

	private JFrame frmCompilator;
	private JButton btnSave;
	private JButton btnCompile;
	private JButton btnLoad;
	private JLabel lblProgramName;
	private JTextArea editorPaneProgram;
	private JTextPane editorPaneSymbolTable;
	private JTextPane editorPaneMsgs;

	private File file_selected;
	private JLabel lblNumberLine;
	private JButton btnNew;
	private JTextPane editorPaneSintacticTree;

	private String masm32_path;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frmCompilator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		file_selected = null;
		masm32_path = null;
		undo = new UndoManager();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		frmCompilator = new JFrame();
		frmCompilator.setResizable(false);
		frmCompilator.setTitle("Compilador");
		frmCompilator.setBounds(100, 100, 1200, 700);
		frmCompilator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCompilator.setMinimumSize(new Dimension(1100, 700));
		SpringLayout springLayout = new SpringLayout();
		frmCompilator.getContentPane().setLayout(springLayout);

		lblProgramName = new JLabel("Cree un nuevo documento o cargue su programa");
		springLayout.putConstraint(SpringLayout.NORTH, lblProgramName, 10, SpringLayout.NORTH, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblProgramName, 10, SpringLayout.WEST, frmCompilator.getContentPane());
		lblProgramName.setAlignmentY(Component.TOP_ALIGNMENT);
		frmCompilator.getContentPane().add(lblProgramName);

		JPanel panel = new JPanel();
		springLayout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblProgramName, -64, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, panel, 34, SpringLayout.NORTH, lblProgramName);
		springLayout.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, lblProgramName);
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		frmCompilator.getContentPane().add(panel);

		btnNew = new JButton("Nuevo");
		btnNew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				final JFileChooser dialog = new JFileChooser (FileSystemView.getFileSystemView().getHomeDirectory());
				dialog.setApproveButtonText("Create");
				dialog.setApproveButtonMnemonic('c');
				dialog.setApproveButtonToolTipText("Create a new file \"txt\"");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("\".txt\"", "txt", "text");
				dialog.setFileFilter(filter);
				dialog.setDialogTitle(SEARCH_MSG);
				int returnValue = dialog.showOpenDialog(null);
				String path_and_name = "";
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					file_selected = dialog.getSelectedFile();
					path_and_name = file_selected.getAbsolutePath().trim();
					String ext = path_and_name.substring(path_and_name.lastIndexOf('.')+1, path_and_name.length());
					if (!ext.equals("txt") || !path_and_name.contains("."))
						path_and_name += ".txt";
					File file = new File(path_and_name);
					file_selected = file;
					FileUtils.reset();
					try {
						if(file.createNewFile()){
							String nombre_archivo = path_and_name.substring(path_and_name.lastIndexOf("\\")+1);
							editorPaneProgram.setEditable(true);
							editorPaneProgram.setText(FileUtils.getContent());
							lblProgramName.setText(nombre_archivo);
							JOptionPane.showMessageDialog(new JFrame(), FILE_CREATED_MSG, "Creado", JOptionPane.PLAIN_MESSAGE);
						}else
							JOptionPane.showMessageDialog(new JFrame(), FILE_ALREADY_EXIST_MSG, "Archivo ya existente", JOptionPane.PLAIN_MESSAGE);
					} catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(new JFrame(), FILE_CREATE_ERROR_MSG, "Warning", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		panel.add(btnNew);
		btnNew.setAlignmentY(Component.TOP_ALIGNMENT);

		btnLoad = new JButton("Cargar");
		panel.add(btnLoad);
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				final JFileChooser dialog = new JFileChooser (FileSystemView.getFileSystemView().getHomeDirectory());
				FileNameExtensionFilter filter = new FileNameExtensionFilter("\".txt\"", "txt", "text");
				dialog.setFileFilter(filter);
				dialog.setDialogTitle(SEARCH_MSG);
				String path_to_file = "";
				if ( dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					file_selected = dialog.getSelectedFile();
					path_to_file = file_selected.getAbsolutePath().trim();
					String nombre_archivo = path_to_file.substring(path_to_file.lastIndexOf("\\")+1);
					int readed = FileUtils.openFile(file_selected);
					if (readed == FileUtils.LOAD_CORRECT) {
						editorPaneProgram.setEditable(true);
						editorPaneProgram.setText(FileUtils.getContent());
						lblProgramName.setText(nombre_archivo);
					}
					else if (readed == FileUtils.FILE_NOT_FOUND)
						JOptionPane.showMessageDialog(new JFrame(), FILE_NOT_FOUND_MSG, "Warning", JOptionPane.ERROR_MESSAGE);
					else
						JOptionPane.showMessageDialog(new JFrame(), FILE_LOAD_ERROR_MSG, "Warning", JOptionPane.ERROR_MESSAGE);

				}
			}
		});
		btnLoad.setAlignmentY(Component.TOP_ALIGNMENT);
		btnLoad.setAlignmentX(Component.RIGHT_ALIGNMENT);

		btnSave = new JButton("Guardar");
		panel.add(btnSave);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (file_selected == null) {
					final JFileChooser dialog = new JFileChooser (FileSystemView.getFileSystemView().getHomeDirectory());
					dialog.setApproveButtonText("Save");
					dialog.setApproveButtonMnemonic('S');
					dialog.setApproveButtonToolTipText("Save a file \"txt\"");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("\".txt\"", "txt", "text");
					dialog.setFileFilter(filter);
					dialog.setDialogTitle(SEARCH_MSG);
					int returnValue = dialog.showOpenDialog(null);
					String path_and_name = "";
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						file_selected = dialog.getSelectedFile();
						path_and_name = file_selected.getAbsolutePath().trim();
						String ext = path_and_name.substring(path_and_name.lastIndexOf('.')+1, path_and_name.length());
						if (ext.equals("ext") || !path_and_name.contains("."))
							path_and_name += ".txt";
						File file = new File(path_and_name);
						file_selected = file;
						FileUtils.reset();
						try {
							if(file.createNewFile()){
								String nombre_archivo = path_and_name.substring(path_and_name.lastIndexOf("\\")+1);
								editorPaneProgram.setEditable(true);
								editorPaneProgram.setText(FileUtils.getContent());
								lblProgramName.setText(nombre_archivo);
								JOptionPane.showMessageDialog(new JFrame(), FILE_CREATED_MSG, "Creado", JOptionPane.PLAIN_MESSAGE);
							}
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(new JFrame(), FILE_CREATE_ERROR_MSG, "Warning", JOptionPane.ERROR_MESSAGE);
						}
					}						
				}
				if (file_selected != null) {
					int writed = FileUtils.saveFile(file_selected, editorPaneProgram.getText());
					if (writed == FileUtils.SAVE_CORRECT)
						JOptionPane.showMessageDialog(new JFrame(), SAVE_CORRECT_MSG, "Guardado", JOptionPane.PLAIN_MESSAGE);
					else
						JOptionPane.showMessageDialog(new JFrame(), SAVE_ERROR_MSG, "Warning", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSave.setAlignmentY(Component.TOP_ALIGNMENT);
		btnSave.setAlignmentX(Component.RIGHT_ALIGNMENT);

		btnCompile = new JButton("Compilar");
		panel.add(btnCompile);
		btnCompile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (file_selected != null ) {
					List<Integer> programBuffer = new ArrayList<Integer>();
					String text = editorPaneProgram.getText().replaceAll("\r", ""); //Esto ahora el problema del doble símbolo para el salto de línea
					for (int i = 0; i < text.length(); i++)
						programBuffer.add((int)text.charAt(i));
					programBuffer.add(END_OF_TEXT); //Caracter de fin de texto en ASCII, tiene que coordinar con el que se usa en Lexicon
					Compilator compilator = Compilator.getInstance(programBuffer);
					compilator.compilate();
					editorPaneMsgs.setText(compilator.getMsgStack().toString());
					loadSymbolTable(compilator.getSymbTable());
					if (!compilator.hasErrors()) {
						editorPaneSintacticTree.setText(compilator.getSyntacticTree().toString());
						// Guardado del archivo Assembler (.ASM)
						String path_to_file = file_selected.getAbsolutePath().trim();
						String nombre_archivo = path_to_file.substring(path_to_file.lastIndexOf("\\") + 1);
						if (nombre_archivo.contains("."))
							nombre_archivo = nombre_archivo.substring(0, nombre_archivo.lastIndexOf("."));
						path_to_file = path_to_file.substring(0, path_to_file.lastIndexOf("\\") + 1); //Path queda con el último slash previo el nombre
						File file = new File(path_to_file + nombre_archivo + ".asm");
						FileUtils.saveFile(file, compilator.getAssemblerCode().toString());
						JOptionPane.showMessageDialog(new JFrame(), ASSEMBLER_GENERATED, "Ensamblador generado", JOptionPane.PLAIN_MESSAGE);
						System.out.println(file.getAbsolutePath());
						if (askMakeExecutable()) {
							if (masm32_path == null || !isMasmPath(masm32_path)) 
								masm32_path = askMasmPath();
							if (isMasmPath(masm32_path)) { 
								boolean result = generateExecutable(path_to_file, nombre_archivo, masm32_path);
								if (result)
									JOptionPane.showMessageDialog(new JFrame(), CORRECT_COMPILATION, "Felicitaciones", JOptionPane.PLAIN_MESSAGE);
								else
									JOptionPane.showMessageDialog(new JFrame(), INCORRECT_COMPILATION, "Warning", JOptionPane.ERROR_MESSAGE);
							}
							else
								JOptionPane.showMessageDialog(new JFrame(), INCORRECT_PATH, "Warning", JOptionPane.ERROR_MESSAGE);
						}
					}			
				} else
					JOptionPane.showMessageDialog(new JFrame(), FILE_NOT_STORED, "Warning", JOptionPane.ERROR_MESSAGE);
			}

		});
		btnCompile.setAlignmentY(Component.TOP_ALIGNMENT);
		btnCompile.setAlignmentX(Component.RIGHT_ALIGNMENT);

		lblNumberLine = new JLabel("L\u00EDnea seleccionada:");
		springLayout.putConstraint(SpringLayout.WEST, panel, 11, SpringLayout.EAST, lblNumberLine);
		springLayout.putConstraint(SpringLayout.WEST, lblNumberLine, 10, SpringLayout.WEST, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblNumberLine, -424, SpringLayout.EAST, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblNumberLine, 6, SpringLayout.SOUTH, lblProgramName);
		lblNumberLine.setAlignmentY(Component.TOP_ALIGNMENT);
		frmCompilator.getContentPane().add(lblNumberLine);
		
		JScrollPane scrollPane_Codigo = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_Codigo, 6, SpringLayout.SOUTH, lblNumberLine);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_Codigo, 10, SpringLayout.WEST, frmCompilator.getContentPane());
		scrollPane_Codigo.setAlignmentY(Component.TOP_ALIGNMENT);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_Codigo, -10, SpringLayout.SOUTH, frmCompilator.getContentPane());
		frmCompilator.getContentPane().add(scrollPane_Codigo);

		editorPaneProgram = new JTextArea();
		editorPaneProgram.setTabSize(2);
		editorPaneProgram.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				int pos = e.getDot();
				int row = 1;
				String text = editorPaneProgram.getText().replaceAll("\r", "");
				for(int i = 0; i < pos; i++){
					if(text.charAt(i) == '\n'){ //new line
						row++;
					}
				}
				lblNumberLine.setText(LINE_COUNTER_MSG + row);
			}
		});
		Document doc = editorPaneProgram.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}
		});
		editorPaneProgram.getActionMap().put("Undo",
				new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				} catch (CannotUndoException e) {
				}
			}
		});
		editorPaneProgram.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		editorPaneProgram.getActionMap().put("Redo",
				new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canRedo()) {
						undo.redo();
					}
				} catch (CannotRedoException e) {
				}
			}
		});
		editorPaneProgram.getInputMap().put(KeyStroke.getKeyStroke("control shift Z"), "Redo");

		scrollPane_Codigo.setViewportView(editorPaneProgram);

		JScrollPane scrollPane_TS = new JScrollPane();
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_Codigo, -6, SpringLayout.WEST, scrollPane_TS);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_TS, 519, SpringLayout.WEST, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_TS, 13, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_TS, -10, SpringLayout.EAST, frmCompilator.getContentPane());
		scrollPane_TS.setMaximumSize(new Dimension(403, 162));
		scrollPane_TS.setMinimumSize(new Dimension(403, 162));
		scrollPane_TS.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane_TS.setAlignmentX(Component.RIGHT_ALIGNMENT);
		frmCompilator.getContentPane().add(scrollPane_TS);

		editorPaneSymbolTable = new JTextPane();
		editorPaneSymbolTable.setAlignmentY(Component.TOP_ALIGNMENT);
		editorPaneSymbolTable.setAlignmentX(Component.RIGHT_ALIGNMENT);
		scrollPane_TS.setViewportView(editorPaneSymbolTable);
		editorPaneSymbolTable.setEditable(false);

		JLabel labelTablaSimbolos = new JLabel("Tabla de s\u00EDmbolos");
		scrollPane_TS.setColumnHeaderView(labelTablaSimbolos);
		labelTablaSimbolos.setAlignmentY(Component.TOP_ALIGNMENT);
		labelTablaSimbolos.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JScrollPane scrollPane_WarningYErrores = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_WarningYErrores, 248, SpringLayout.NORTH, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_WarningYErrores, 6, SpringLayout.EAST, scrollPane_Codigo);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_WarningYErrores, -10, SpringLayout.EAST, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_TS, -6, SpringLayout.NORTH, scrollPane_WarningYErrores);
		scrollPane_WarningYErrores.setMinimumSize(new Dimension(403, 162));
		scrollPane_WarningYErrores.setMaximumSize(new Dimension(403, 162));
		scrollPane_WarningYErrores.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane_WarningYErrores.setAlignmentX(Component.RIGHT_ALIGNMENT);
		frmCompilator.getContentPane().add(scrollPane_WarningYErrores);

		editorPaneMsgs = new JTextPane();
		editorPaneMsgs.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editorPaneMsgs.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane_WarningYErrores.setViewportView(editorPaneMsgs);
		editorPaneMsgs.setEditable(false);

		JLabel lblWarningsYErrores = new JLabel("Warnings y errores");
		scrollPane_WarningYErrores.setColumnHeaderView(lblWarningsYErrores);
		lblWarningsYErrores.setAlignmentY(Component.TOP_ALIGNMENT);
		lblWarningsYErrores.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JScrollPane scrollPane_ArbolSintactico = new JScrollPane();
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_ArbolSintactico, 6, SpringLayout.EAST, scrollPane_Codigo);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_ArbolSintactico, -7, SpringLayout.EAST, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_WarningYErrores, -6, SpringLayout.NORTH, scrollPane_ArbolSintactico);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_ArbolSintactico, 362, SpringLayout.NORTH, frmCompilator.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_ArbolSintactico, -10, SpringLayout.SOUTH, frmCompilator.getContentPane());
		frmCompilator.getContentPane().add(scrollPane_ArbolSintactico);

		editorPaneSintacticTree = new JTextPane();
		scrollPane_ArbolSintactico.setViewportView(editorPaneSintacticTree);

		JLabel lblrbolSintctico = new JLabel("\u00C1rbol sint\u00E1ctico");
		scrollPane_ArbolSintactico.setColumnHeaderView(lblrbolSintctico);
	}

	private void loadSymbolTable(Hashtable<String, ElementoTS> symbolTable) {
		String text = "";
		Set<String> keys = symbolTable.keySet();
		for(String key:keys) {
			ElementoTS elem = symbolTable.get(key);
			text += "Token: " + elem.getTokenClass() + " - Tipo: " + elem.getVariableType() + 
					" - Lexema: " + key + " - Valor: " + elem.getValue() + " - Repeticiones: " + elem.getVariableRepetitions() +
					" - Declarada: " + elem.isDeclared() + " - Estructura: " + elem.getIdentifierClass() +
					" - Tamaño en Bytes(Colecciones): " + elem.getCSizeBytes() + " - Elementos en colección: " + elem.getElemsCollection() +
					" - Id único(Cadenas): " + elem.getId() + " - Es puntero: " + elem.isPointer() + "\n";
			text += "------------------------------------------------- \n"; // Separación
		}
		editorPaneSymbolTable.setText(text);
	}

	
	private boolean generateExecutable(String path, String file_name, String masm32_path) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		String command = "cd " + path + " && " 
				+ masm32_path + "\\ml.exe /c /coff " + file_name + ".asm &&"
				+ " " + masm32_path + "\\link.exe /subsystem:windows " + file_name + ".obj";
		System.out.println(command);
		processBuilder.command("cmd.exe", "/c", command);
		try {
			//// ML ////
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			StringBuilder output = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
				output.append(line + "\n");
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
				return true;
			} else {
				System.out.println("Abnormal termination");
				System.out.println(output);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String askMasmPath() {
		String masm_path = "";
		JFileChooser chooser = new JFileChooser("C:\\"); 
		chooser.setDialogTitle(SELECT_FOLDER);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			masm_path = chooser.getCurrentDirectory().getAbsolutePath();
		else
			JOptionPane.showMessageDialog(new JFrame(), INVALID_SELECTION, "Warning", JOptionPane.ERROR_MESSAGE);
		return masm_path;
	}

	private boolean isMasmPath(String masm32_path) {
		File f_ml = new File(masm32_path + "\\ml.exe");
		File f_linker = new File(masm32_path + "\\link.exe");
		return (f_ml.exists() && !f_ml.isDirectory() && f_linker.exists() && !f_linker.isDirectory());
	}

	private boolean askMakeExecutable() {
		int i = JOptionPane.showOptionDialog(new JFrame(), ASK_TO_MAKE_EXECUTABLE, "Selecciona una opción",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (i == 0)
			return true;
		else
			return false;
	}
}
