import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame{
	public static void setFrame(JFrame frame) {
		Main.frame = frame;
	}
private static JFrame frame;
private int tableCount;
private int columnCount;
private int rowCount;
private ResultSetMetaData rsmd;
private Object[] tableNames;
private Object[] headerData;
private Object[][] tableData;
private JTextField[] fieldArray;
private JLabel[] labelArray;
private boolean bool;
// JDBC URL, username and password of MySQL server
private final String database = "superstore" ;
private final String _url = "jdbc:mysql://localhost:3306/"+database+"?useSSL=false";
private String url=_url;
private final String _user = "root";
private String user=_user;
private final String _password = "";
private String password = _password;
private final String _query = "SELECT * FROM Branch";
private String query = _query;
//data components
private JPanel dataPanel;
private GridBagLayout gbl_dataPanel;
// JDBC variables for opening and managing connection
private Connection con;
private Statement stmt;
private ResultSet rs;
private JTable table;
private DefaultTableModel model;
private JTextField textField;
private JPanel tablePanel;
private JScrollPane scroll;
private JButton btnSearch;
private JPanel submisionPanel;
private JComboBox queryCombo;
private JPopupMenu popupMenu;
private JMenuItem menuItemAdd;
private JMenuItem menuItemRemove;

	public Main() {
		super("Main");
		initialize();
		setBounds(300, 100, 800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}
	
	private void initialize() {
		//JOptionPane.QUESTION_MESSAGE
	    /*
	     * url = JOptionPane.showInputDialog(panel, "Please enter connection url", _url);
	    user = JOptionPane.showInputDialog(panel, "Please enter Username", _user);
	    password = JOptionPane.showInputDialog(panel, "Please enter Password", _password);
	    query = JOptionPane.showInputDialog(panel, "Please enter query", _query);
	     * 
	     * */
		myData();  		
	}

	private void myData() {			
		dataPanel = new JPanel();
	    gbl_dataPanel = new GridBagLayout();
	    gbl_dataPanel.columnWidths = new int[]{56, 124, 0};
	    gbl_dataPanel.rowHeights = new int[]{19, 20, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 0};
	    gbl_dataPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
	    gbl_dataPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
	    dataPanel.setLayout(gbl_dataPanel);		    
        getContentPane().setLayout(null);
        dataPanel.setBounds(15, 65, 300, 329);
        getContentPane().add(dataPanel);
		printTable(query);
        content();
	}

	private void printTable(String query) {
		table = new JTable();
		
		try {
            // opening database connection to MySQL server
        	con = DriverManager.getConnection(url, user, password);
            // getting Statement object to execute query
            stmt = con.createStatement();
            // executing SELECT query
            rs = stmt.executeQuery(query);
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            fieldArray = new JTextField[columnCount];
            labelArray = new JLabel[columnCount];
            bool = rs.last();//required
			rowCount = rs.getRow();
            rs.beforeFirst();
            
            
            tableData = new Object[rowCount][columnCount];
            headerData = new Object[columnCount];
            model = new DefaultTableModel();
            for(int i=1;i<=columnCount;i++) {
            	headerData[i-1]=rsmd.getColumnName(i);
            	//header.setBackground(Color.yellow);
            	model.addColumn(headerData[i-1]);
            	//entry(headerData[i-1]+"", "", i);
            	}
            int count = 0;
            while (rs.next()) {
                for(int i = 1; i <= columnCount; i++){
                	tableData[count][i-1]=rs.getString(i);
                }
            	model.addRow(tableData[count]);
                count++;
            } 
            
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '"+database+"'";
            ResultSet rs = stmt.executeQuery(sql);	
            while (rs.next()) {
              tableCount = rs.getInt(1);
            }
            rs.close();
            
            tableNames = new Object[tableCount];
            
            // --- LISTING DATABASE TABLE NAMES ---
            String[] types = { "TABLE" };
            rs = con.getMetaData().getTables(database, null, "%", types);
            int c=0;
            while (rs.next()) {
              tableNames[c] = rs.getString(3);
              c++;
            }
            rs.close();

              } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
		
		
        entry(labelArray, fieldArray);
		//rest
		table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablePanel = new JPanel();
        tablePanel.setBounds(300, 60, 500, 500);
        tablePanel.setLayout(new FlowLayout());
        scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tablePanel.add(scroll);
        getContentPane().add(tablePanel);

        table.setCellSelectionEnabled(true);
        table.addMouseListener(new MouseAdapter() {
        	 @Override
        	    public void mouseClicked(MouseEvent e) {
        		 	int row_index = table.rowAtPoint(e.getPoint());
        		 	for(int i=0;i<columnCount;i++) {
        		 		fieldArray[i].setText((String) table.getModel().getValueAt(row_index, i));
        		 	}
        		 	
        	    }
        });
	
	
	
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemAdd = new JMenuItem("Add New Row");
        JMenuItem menuItemRemove = new JMenuItem("Remove Current Row");
        JMenuItem menuItemRemoveAll = new JMenuItem("Remove All Rows");
         
        popupMenu.add(menuItemAdd);
        popupMenu.add(menuItemRemove);
        popupMenu.add(menuItemRemoveAll);
        table.setComponentPopupMenu(popupMenu);
        
        table.addMouseListener(new MouseAdapter() {
        	@Override
            public void mousePressed(MouseEvent event) {
                // selects the row at which point the mouse is clicked
                Point point = event.getPoint();
                int currentRow = table.rowAtPoint(point);
                table.setRowSelectionInterval(currentRow, currentRow);
            }
        });	
	}	
	
	
	private void entry(JLabel[] labelArray, JTextField[] fieldArray) {
        for(int i=0;i<columnCount;i++) {
        	labelArray[i] = new JLabel(headerData[i]+"");
        	fieldArray[i] = new JTextField();
            GridBagConstraints gbc_label = new GridBagConstraints();
            gbc_label.fill = GridBagConstraints.HORIZONTAL;
            gbc_label.insets = new Insets(0, 0, 5, 5);
            gbc_label.gridx = 0;
            gbc_label.gridy = i;
            dataPanel.add(labelArray[i], gbc_label);
 
            fieldArray[i].setColumns(10);
            GridBagConstraints gbc_textField = new GridBagConstraints();
            gbc_textField.anchor = GridBagConstraints.EAST;
            gbc_textField.insets = new Insets(0, 0, 5, 0);
            gbc_textField.gridx = 1;
            gbc_textField.gridy = i;
            dataPanel.add(fieldArray[i], gbc_textField);
        }
	}

	private void content() {
		JPanel logoutPanel = new JPanel();
        logoutPanel.setBounds(715, 6, 88, 47);
        logoutPanel.setLayout(null);
        JButton button = new JButton("logout");
        button.setBounds(0, 10, 78, 25);
        logoutPanel.add(button);
        getContentPane().add(logoutPanel);
        submissionPanel();
		JPanel queryPanel = new JPanel();
        queryPanel.setBorder(new TitledBorder(null, "database tables", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        queryPanel.setBounds(69, 0, 642, 47);
        getContentPane().add(queryPanel);
        queryPanel.setLayout(null);
        queryCombo = new JComboBox(tableNames);
        queryCombo.setBounds(20, 20, 120, 20);
        queryCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(e.getSource() == queryCombo) {
            		dataPanel.removeAll();
            		tablePanel.setVisible(false);
        			String sel = queryCombo.getItemAt(queryCombo.getSelectedIndex())+"";
        			query = "SELECT * FROM "+sel;
        			printTable(query);
        			revalidate();
        			repaint();
        		}
            }
        });
        queryPanel.add(queryCombo);
	}

	private void submissionPanel() {
		//submisionPanel
        submisionPanel = new JPanel();
        submisionPanel.setBounds(69, 450, 171, 47);
        getContentPane().add(submisionPanel);
        submisionPanel.setLayout(null);
        
        JButton insert = new JButton("insert");
        insert.setBounds(5, 17, 82, 25);
        submisionPanel.add(insert);
        
        JButton clear = new JButton("clear");
        clear.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(e.getSource() == clear) {
        			for(int i=0;i<columnCount;i++) {
        				fieldArray[i].setText("");
        			}
        		}
        	}
        });
        clear.setBounds(99, 17, 67, 25);
        submisionPanel.add(clear);		
	}

	//main
	public static void main(String args[]) {
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setFrame(new Main());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
    }
}
