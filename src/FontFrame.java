
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FontFrame extends JFrame implements ActionListener,ListSelectionListener{

	private static final long serialVersionUID = -2946481589092736145L;

	private JLabel fontLabel;
	private JLabel styleLabel;
	private JLabel sizeLabel;
	private JButton okButton;
	private JButton cancel;
	private Font currentFont;
	private NoteFrame nf;
	final JDialog fontDialog;
	final JList fontList, styleList, sizeList;
	final JTextField fontText = new JTextField(9);
	final JTextField styleText = new JTextField(8);
	final JTextField sizeText = new JTextField(5);
	final int style[] = { Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD + Font.ITALIC };
	final String fontStyle[] = { "常规", "粗体", "斜体", "粗斜体" };
	final String fontSize[] = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36",
			"48", "72" };
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	final String fontName[] = ge.getAvailableFontFamilyNames();
	final JLabel sample = new JLabel("AAAaaaBBBcccc");

	public FontFrame(NoteFrame nf) {
		this.nf = nf;
		fontDialog = new JDialog(nf, "字体设置", false);
		Container con = fontDialog.getContentPane();
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		fontLabel = new JLabel("字体(F)：");
		fontLabel.setPreferredSize(new Dimension(100, 20));// 构造一个Dimension，并将其初始化为指定宽度和高度
		styleLabel = new JLabel("字形(Y)：");
		styleLabel.setPreferredSize(new Dimension(100, 20));
		sizeLabel = new JLabel("大小(S)：");
		sizeLabel.setPreferredSize(new Dimension(100, 20));
		fontText.setPreferredSize(new Dimension(200, 20));
		currentFont = nf.getTxtArea().getFont();
		fontText.setText(currentFont.getFontName());
		fontText.selectAll();
		styleText.setPreferredSize(new Dimension(200, 20));
		sizeText.setPreferredSize(new Dimension(200, 20));
		okButton = new JButton("确定");
		okButton.addActionListener(this);
		cancel = new JButton("取消");
		cancel.addActionListener(this);
		chooseFontStyle();
		styleText.selectAll();
		String str = String.valueOf(currentFont.getSize());
		sizeText.setText(str);
		sizeText.selectAll();
		fontList = new JList(fontName);
		fontList.setFixedCellWidth(86);
		fontList.setFixedCellHeight(20);
		fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		styleList = new JList(fontStyle);
		styleList.setFixedCellWidth(86);
		styleList.setFixedCellHeight(20);
		styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		styleListSetSelectedIndex();
		sizeList = new JList(fontSize);
		sizeList.setFixedCellWidth(43);
		sizeList.setFixedCellHeight(20);
		sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontList.addListSelectionListener(this);
		styleList.addListSelectionListener(this);
		sizeList.addListSelectionListener(this);
		JPanel samplePanel = new JPanel();
		samplePanel.setBorder(BorderFactory.createTitledBorder("示例"));
		samplePanel.add(sample);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		panel1.add(fontLabel);
		panel1.add(styleLabel);
		panel1.add(sizeLabel);
		panel2.add(fontText);
		panel2.add(styleText);
		panel2.add(sizeText);
		panel3.add(new JScrollPane(fontList));// JList不支持直接滚动，所以要让JList作为JScrollPane的视口视图
		panel3.add(new JScrollPane(styleList));
		panel3.add(new JScrollPane(sizeList));
		panel4.add(okButton);
		panel4.add(cancel);
		con.add(panel1);
		con.add(panel2);
		con.add(panel3);
		con.add(samplePanel);
		con.add(panel4,BorderLayout.SOUTH);
		fontDialog.setSize(350, 400);
		fontDialog.setLocation(200, 200);
		fontDialog.setResizable(false);
		fontDialog.setVisible(true);
	}

	private void styleListSetSelectedIndex() {
		if (currentFont.getStyle() == Font.PLAIN) {
			styleList.setSelectedIndex(0);
		}
		else if (currentFont.getStyle() == Font.BOLD) {
			styleList.setSelectedIndex(1);
		}
		else if (currentFont.getStyle() == Font.ITALIC) {
			styleList.setSelectedIndex(2);
		}
		else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC)) {
			styleList.setSelectedIndex(3);
		}
	}

	private void chooseFontStyle() {
		if (currentFont.getStyle() == Font.PLAIN) {
			styleText.setText("常规");
		}
		else if (currentFont.getStyle() == Font.BOLD) {
			styleText.setText("粗体");
		}
		else if (currentFont.getStyle() == Font.ITALIC) {
			styleText.setText("斜体");
		}
		else if (currentFont.getStyle() == (Font.BOLD + Font.ITALIC)) {
			styleText.setText("粗斜体");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton) {
			Font okFont = new Font(fontText.getText(), style[styleList.getSelectedIndex()],
					Integer.parseInt(sizeText.getText()));
			nf.getTxtArea().setFont(okFont);
			fontDialog.dispose();

		}
		if(e.getSource() == cancel) {
			fontDialog.dispose();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == fontList) {
			fontText.setText(fontName[fontList.getSelectedIndex()]);
			fontText.selectAll();
			Font sampleFont1 = new Font(fontText.getText(), style[styleList.getSelectedIndex()],
					Integer.parseInt(sizeText.getText()));
			sample.setFont(sampleFont1);
		}
		if(e.getSource() == styleList) {
			int s = style[styleList.getSelectedIndex()];
			styleText.setText(fontStyle[s]);
			styleText.selectAll();
			Font sampleFont2 = new Font(fontText.getText(), style[styleList.getSelectedIndex()],
					Integer.parseInt(sizeText.getText()));
			sample.setFont(sampleFont2);
		}
		if(e.getSource() == sizeList) {
			sizeText.setText(fontSize[sizeList.getSelectedIndex()]);
			// sizeText.requestFocus();
			sizeText.selectAll();
			Font sampleFont3 = new Font(fontText.getText(), style[styleList.getSelectedIndex()],
					Integer.parseInt(sizeText.getText()));
			sample.setFont(sampleFont3);
		}
	}
}
