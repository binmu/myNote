
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class NoteFrame extends JFrame implements ActionListener, DocumentListener {

	private static final long serialVersionUID = 72568617577665081L;

	private JPanel pnlMain; // 主面板
	private JPanel pnlTxt; // 文本域面板
	private JMenuBar menuBar; // 菜单条
	private JLabel statusLabel; // 底部状态栏
	private JPanel pnlStatus; // 底部状态栏面板
	private JTextArea txtArea; // 文本域

	public JTextArea getTxtArea() {
		return txtArea;
	}

	// 菜单
	private JMenu menuFile; // 文件
	private JMenu menuEdit; // 编辑
	private JMenu menuFormat; // 格式
	private JMenu menuView; // 视图
	private JMenu menuHelp; // 帮助
	// 文件下的菜单项
	private JMenuItem menuItemNew; // 新建
	private JMenuItem menuItemOpen; // 打开
	private JMenuItem menuItemSave; // 保存
	private JMenuItem menuItemSaveAsOher;// 另存为
	private JMenuItem menuItemExit; // 退出
	// 编辑下的菜单项
	private JMenuItem editMenuUndo; // "撤销(U)"
	private JMenuItem editMenuCut; // "剪切(T)"
	private JMenuItem editMenuCopy; // "复制(C)"
	private JMenuItem editMenuPaste; // "粘帖(P)"
	private JMenuItem editMenuDelete; // "删除(D)"
	private JMenuItem menuItemFind; // 查找
	private JMenuItem menuItemFindNext; // 查找下一个
	private JMenuItem menuItemReplace; // 替换
	private JMenuItem menuItemDate; // 时间/日期
	// 格式下的菜单项
	private JCheckBoxMenuItem menuItemLineWrap; // 自动换行
	private JMenuItem menuItemFont; // 字体
	private JMenuItem menuItemColor; // 背景颜色
	// 视图下的菜单项
	private JCheckBoxMenuItem menuItemStatusBar; // 状态栏

	private JMenuItem helpMenuAboutNotepad; // 关于记事本

	private JPopupMenu popupMenu; // 鼠标右键弹出菜单
	private JMenuItem popupMenuUndo; // "撤销(U)"
	private JMenuItem popupMenuCut; // "剪切(T)"
	private JMenuItem popupMenuCopy; // "复制(C)"
	private JMenuItem popupMenuPaste; // "粘帖(P)"
	private JMenuItem popupMenuDelete; // "删除(D)"
	private JMenuItem popupMenuSelectAll; // "全选(A)"

	// 系统剪贴板
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Clipboard clipBoard = toolkit.getSystemClipboard();
	// 创建撤销操作管理器(与撤销操作有关)
	protected UndoManager undo = new UndoManager();
	protected UndoableEditListener undoHandler = new UndoHandler();
	// 其他变量
	private String oldValue;// 存放编辑区原来的内容，用于比较文本是否有改动
	boolean isNewFile = true;// 是否新文件(未保存过的)
	private File currentFile;// 当前文件名

	public NoteFrame() {
		iniFrame();
		menuBar = new JMenuBar();
		pnlMain = new JPanel(new BorderLayout()); // 主面板
		pnlTxt = new JPanel(new BorderLayout()); // 文本域面板
		txtArea = new JTextArea(20, 50);

		// 创建文件菜单及菜单项并注册事件监听
		createFileItem();
		// 创建编辑菜单及菜单项并注册事件监听
		createEditItem();
		// 创建格式菜单及菜单项并注册事件监听
		createFormatItem();
		// 创建查看菜单及菜单项并注册事件监听
		menuView = new JMenu("视图(V)");
		menuView.setMnemonic('V');
		menuItemStatusBar = new JCheckBoxMenuItem("状态栏(S)");
		menuItemStatusBar.setMnemonic('S');// 设置快捷键ALT+S
		menuItemStatusBar.setState(true);
		menuItemStatusBar.addActionListener(this);
		// 创建帮助菜单及菜单项并注册事件监听
		menuHelp = new JMenu("帮助(H)");
		menuHelp.setMnemonic('H');
		helpMenuAboutNotepad = new JMenuItem("关于记事本(A)");
		helpMenuAboutNotepad.addActionListener(this);
		// 创建右键弹出菜单
		createPopupMenu();

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuFormat);
		menuBar.add(menuView);
		menuBar.add(menuHelp);

		menuFile.add(menuItemNew);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.add(menuItemSaveAsOher);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);

		menuEdit.add(editMenuUndo);
		menuEdit.add(editMenuCut);
		menuEdit.add(editMenuCopy);
		menuEdit.add(editMenuPaste);
		menuEdit.add(editMenuDelete);
		menuEdit.addSeparator();
		menuEdit.add(menuItemFind);
		menuEdit.add(menuItemFindNext);
		menuEdit.add(menuItemReplace);
		menuEdit.addSeparator();
		menuEdit.add(menuItemDate);

		menuFormat.add(menuItemLineWrap);
		menuFormat.add(menuItemFont);
		menuFormat.addSeparator();
		menuFormat.add(menuItemColor);

		menuView.add(menuItemStatusBar);

		menuHelp.add(helpMenuAboutNotepad);

		// 创建文本编辑区并添加滚动条
		JScrollPane scroller = createTxtAreaEdit();
		statusLabel = new JLabel("状态栏");
		pnlStatus = new JPanel(new BorderLayout());
		pnlStatus.add(statusLabel);
		pnlTxt.add(scroller, BorderLayout.CENTER);// 向文本域面板添加文本编辑区
		pnlMain.add(menuBar, BorderLayout.NORTH); // 将菜单栏添加到主面板北部
		pnlMain.add(pnlTxt, BorderLayout.CENTER); // 将文本域面板添加到主面板上
		pnlMain.add(pnlStatus, BorderLayout.SOUTH); // 将菜单栏添加到主面板北部

		this.add(pnlMain); // 将主面板添加到框架上

		// 添加窗口监听器
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitWindowChoose();
			}
		});

		checkMenuItemEnabled();
		txtArea.requestFocus();
	}

	private JScrollPane createTxtAreaEdit() {
		JScrollPane scroller = new JScrollPane(txtArea);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		txtArea.setWrapStyleWord(true);// 设置单词在一行不足容纳时换行
		txtArea.setLineWrap(true);// 设置文本编辑区自动换行默认为true,即会"自动换行"
		oldValue = txtArea.getText();// 获取原文本编辑区的内容
		txtArea.setComponentPopupMenu(popupMenu); // 向文本域添加弹出菜单
		// 文本编辑区注册右键菜单事件
		txtArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {// 返回此鼠标事件是否为该平台的弹出菜单触发事件

					popupMenu.show(e.getComponent(), e.getX(), e.getY());// 在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
				}
				checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除等功能的可用性
				txtArea.requestFocus();// 编辑区获取焦点
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {// 返回此鼠标事件是否为该平台的弹出菜单触发事件

					popupMenu.show(e.getComponent(), e.getX(), e.getY());// 在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
				}
				checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除等功能的可用性
				txtArea.requestFocus();// 编辑区获取焦点
			}
		});
		return scroller;
	}

	private void createPopupMenu() {
		popupMenu = new JPopupMenu();
		popupMenuUndo = new JMenuItem("撤销(U)");
		popupMenuCut = new JMenuItem("剪切(T)");
		popupMenuCopy = new JMenuItem("复制(C)");
		popupMenuPaste = new JMenuItem("粘帖(P)");
		popupMenuDelete = new JMenuItem("删除(D)");
		popupMenuSelectAll = new JMenuItem("全选(A)");

		// 向右键菜单添加菜单项和分隔符
		popupMenu.add(popupMenuUndo);
		popupMenu.addSeparator();
		popupMenu.add(popupMenuCut);
		popupMenu.add(popupMenuCopy);
		popupMenu.add(popupMenuPaste);
		popupMenu.add(popupMenuDelete);
		popupMenu.addSeparator();
		popupMenu.add(popupMenuSelectAll);
		popupMenuUndo.setEnabled(false);
		// 文本编辑区注册右键菜单事件
		popupMenuUndo.addActionListener(this);
		popupMenuCut.addActionListener(this);
		popupMenuCopy.addActionListener(this);
		popupMenuPaste.addActionListener(this);
		popupMenuDelete.addActionListener(this);
		popupMenuSelectAll.addActionListener(this);
	}

	private void createFormatItem() {
		menuFormat = new JMenu("格式(O)");
		menuFormat.setMnemonic('O');
		menuItemLineWrap = new JCheckBoxMenuItem("自动换行(W)");
		menuItemLineWrap.setMnemonic('W');// 设置快捷键ALT+W
		menuItemLineWrap.setState(true);
		menuItemLineWrap.addActionListener(this);
		menuItemFont = new JMenuItem("字体(F)...");
		menuItemFont.addActionListener(this);
		menuItemColor = new JMenuItem("背景颜色...");
		menuItemColor.addActionListener(this);
	}

	private void createEditItem() {
		menuEdit = new JMenu("编辑(E)");
		menuEdit.setMnemonic('E');
		// 当选择编辑菜单时，设置剪切、复制、粘贴、删除等功能的可用性
		menuEdit.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e)// 取消菜单时调用
			{
				checkMenuItemEnabled();// 设置剪切、复制、粘贴、删除等功能的可用性
			}

			public void menuDeselected(MenuEvent e)// 取消选择某个菜单时调用
			{
				checkMenuItemEnabled();// 设置剪切、复制、粘贴、删除等功能的可用性
			}

			public void menuSelected(MenuEvent e)// 选择某个菜单时调用
			{
				checkMenuItemEnabled();// 设置剪切、复制、粘贴、删除等功能的可用性
			}
		});
		editMenuUndo = new JMenuItem("撤销(U)");
		editMenuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		editMenuUndo.addActionListener(this);
		editMenuUndo.setEnabled(false);
		editMenuCut = new JMenuItem("剪切(T)");
		editMenuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		editMenuCut.addActionListener(this);
		editMenuCopy = new JMenuItem("复制(C)");
		editMenuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		editMenuCopy.addActionListener(this);
		editMenuPaste = new JMenuItem("粘贴(P)");
		editMenuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		editMenuPaste.addActionListener(this);
		editMenuDelete = new JMenuItem("删除(D)");
		editMenuDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		editMenuDelete.addActionListener(this);
		menuItemFind = new JMenuItem("查找(F)...");
		menuItemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		menuItemFind.addActionListener(this);
		menuItemFindNext = new JMenuItem("查找下一个(N)");
		menuItemFindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		menuItemFindNext.addActionListener(this);
		menuItemReplace = new JMenuItem("替换(R)...", 'R');
		menuItemReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
		menuItemReplace.addActionListener(this);
		menuItemDate = new JMenuItem("时间/日期(D)", 'D');
		menuItemDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		menuItemDate.addActionListener(this);
	}

	private void createFileItem() {
		menuFile = new JMenu("文件(F)");
		menuFile.setMnemonic('F');
		menuItemNew = new JMenuItem("新建(N)");
		menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuItemNew.addActionListener(this);
		menuItemOpen = new JMenuItem("打开(O)...");
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuItemOpen.addActionListener(this);
		menuItemSave = new JMenuItem("保存(S)");
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuItemSave.addActionListener(this);
		menuItemSaveAsOher = new JMenuItem("另存为(A)...");
		menuItemSaveAsOher.addActionListener(this);
		menuItemExit = new JMenuItem("退出(X)");
		menuItemExit.addActionListener(this);
	}

	// 关闭窗口时调用
	public void exitWindowChoose() {
		txtArea.requestFocus();
		String currentValue = txtArea.getText();
		if (currentValue.equals(oldValue) == true) {
			System.exit(0);
		} else {
			int exitChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存，是否保存？", "退出提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (exitChoose == JOptionPane.YES_OPTION) { // boolean isSave=false;
				if (isNewFile) {
					String str = null;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("确定");
					fileChooser.setDialogTitle("另存为");

					int result = fileChooser.showSaveDialog(this);

					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel.setText("　您没有保存文件");
						return;
					}

					File saveFileName = fileChooser.getSelectedFile();

					if (saveFileName == null || saveFileName.getName().equals("")) {
						JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							FileWriter fw = new FileWriter(saveFileName);
							BufferedWriter bfw = new BufferedWriter(fw);
							bfw.write(txtArea.getText(), 0, txtArea.getText().length());
							bfw.flush();
							fw.close();

							isNewFile = false;
							currentFile = saveFileName;
							oldValue = txtArea.getText();

							this.setTitle(saveFileName.getName() + "  - 记事本");
							statusLabel.setText("　当前打开文件:" + saveFileName.getAbsoluteFile());
							// isSave=true;
						} catch (IOException ioException) {
						}
					}
				} else {
					try {
						FileWriter fw = new FileWriter(currentFile);
						BufferedWriter bfw = new BufferedWriter(fw);
						bfw.write(txtArea.getText(), 0, txtArea.getText().length());
						bfw.flush();
						fw.close();
					} catch (IOException ioException) {
					}
				}
				System.exit(0);
			} else if (exitChoose == JOptionPane.NO_OPTION) {
				System.exit(0);
			} else {
				return;
			}
		}
	}

	// 设置菜单项的可用性：剪切，复制，粘帖，删除功能
	public void checkMenuItemEnabled() {
		String selectText = txtArea.getSelectedText();
		if (selectText == null) {
			editMenuCut.setEnabled(false);
			popupMenuCut.setEnabled(false);
			editMenuCopy.setEnabled(false);
			popupMenuCopy.setEnabled(false);
			editMenuDelete.setEnabled(false);
			popupMenuDelete.setEnabled(false);
		} else {
			editMenuCut.setEnabled(true);
			popupMenuCut.setEnabled(true);
			editMenuCopy.setEnabled(true);
			popupMenuCopy.setEnabled(true);
			editMenuDelete.setEnabled(true);
			popupMenuDelete.setEnabled(true);
		}
		// 粘帖功能可用性判断
		Transferable contents = clipBoard.getContents(this);
		if (contents == null) {
			editMenuPaste.setEnabled(false);
			popupMenuPaste.setEnabled(false);
		} else {
			editMenuPaste.setEnabled(true);
			popupMenuPaste.setEnabled(true);
		}
	}// 方法checkMenuItemEnabled()结束

	private void iniFrame() {

		// 给窗体初始化一些属性
		this.setTitle("懿子记事本");
		this.setSize(500, 500);
		// 获取默认的窗体工具箱，设置图标
		Toolkit toolKit = Toolkit.getDefaultToolkit();
		Image image = toolKit.getImage("image/logo.png");
		this.setIconImage(image);
		this.setLocationRelativeTo(null);// 窗体位置相对于屏幕，出现在屏幕中央
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == menuItemNew) {
			newFile();
		}
		if (e.getSource() == menuItemOpen) {
			openFile();
		}
		if (e.getSource() == menuItemSave) {
			saveFile();
		}
		if (e.getSource() == menuItemSaveAsOher) {
			saveAsOther();
		}
		if (e.getSource() == menuItemExit) {
			int exitChoose = JOptionPane.showConfirmDialog(this, "确定要退出吗?", "退出提示", JOptionPane.OK_CANCEL_OPTION);
			if (exitChoose == JOptionPane.OK_OPTION) {
				System.exit(0);
			} else {
				return;
			}
		}
		if (e.getSource() == editMenuUndo || e.getSource() == popupMenuUndo) {
			txtArea.requestFocus();
			if (undo.canUndo()) {
				try {
					undo.undo();
				} catch (CannotUndoException ex) {
					System.out.println("Unable to undo:" + ex);
				}
			}
			if (!undo.canUndo()) {
				editMenuUndo.setEnabled(false);
			}
		}
		if (e.getSource() == editMenuCut || e.getSource() == popupMenuCut) {
			txtArea.requestFocus();
			String text = txtArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			txtArea.replaceRange("", txtArea.getSelectionStart(), txtArea.getSelectionEnd());
			checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除功能的可用性
		}
		if (e.getSource() == editMenuCopy || e.getSource() == popupMenuCopy) {
			txtArea.requestFocus();
			String text = txtArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipBoard.setContents(selection, null);
			checkMenuItemEnabled();// 设置剪切，复制，粘帖，删除功能的可用性
		}
		if (e.getSource() == editMenuPaste || e.getSource() == popupMenuPaste) {
			txtArea.requestFocus();
			Transferable contents = clipBoard.getContents(this);
			if (contents == null)
				return;
			String text = "";
			try {
				text = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception exception) {
			}
			txtArea.replaceRange(text, txtArea.getSelectionStart(), txtArea.getSelectionEnd());
			checkMenuItemEnabled();
		}
		if (e.getSource() == editMenuDelete || e.getSource() == popupMenuDelete) {
			txtArea.requestFocus();
			txtArea.replaceRange("", txtArea.getSelectionStart(), txtArea.getSelectionEnd());
			checkMenuItemEnabled(); // 设置剪切、复制、粘贴、删除等功能的可用性
		}
		if (e.getSource() == menuItemFind) {
			txtArea.requestFocus();
			new FindFrame(this);
		}
		if (e.getSource() == menuItemFindNext) {
			txtArea.requestFocus();
			new FindFrame(this);
		}
		if (e.getSource() == menuItemReplace) {
			txtArea.requestFocus();
			new ReplaceFrame(this);
		}
		if (e.getSource() == menuItemDate) {
			txtArea.requestFocus();
			Calendar rightNow = Calendar.getInstance();
			Date date = rightNow.getTime();
			txtArea.insert(date.toString(), txtArea.getCaretPosition());
		}
		if (e.getSource() == popupMenuSelectAll) {
			txtArea.selectAll();
		}
		if (e.getSource() == menuItemLineWrap) {
			if (menuItemLineWrap.getState())
				txtArea.setLineWrap(true);
			else
				txtArea.setLineWrap(false);
		}
		if (e.getSource() == menuItemFont) {
			txtArea.requestFocus();
			new FontFrame(this);
		}
		if (e.getSource() == menuItemColor) {
			Color color = JColorChooser.showDialog(this, "颜色设置", null);
			txtArea.setBackground(color);
		}
		if (e.getSource() == menuItemStatusBar) {
			if (menuItemStatusBar.getState())
				statusLabel.setVisible(true);
			else
				statusLabel.setVisible(false);
		}
		if (e.getSource() == helpMenuAboutNotepad) {
			txtArea.requestFocus();
			JOptionPane.showMessageDialog(this,
					"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n" + "你爸爸编写于史前一万年，有意见驳回，因为你爸爸就是你爸爸\n"
							+ "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n",
					"记事本", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void saveAsOther() {
		txtArea.requestFocus();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("另存为");
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) {
			statusLabel.setText("　您没有选择任何文件");
			return;
		}
		File saveFileName = fileChooser.getSelectedFile();
		if (saveFileName == null || saveFileName.getName().equals("")) {
			JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				FileWriter fw = new FileWriter(saveFileName);
				BufferedWriter bfw = new BufferedWriter(fw);
				bfw.write(txtArea.getText(), 0, txtArea.getText().length());
				bfw.flush();
				fw.close();
				oldValue = txtArea.getText();
				this.setTitle(saveFileName.getName() + "  - 记事本");
				statusLabel.setText("　当前打开文件:" + saveFileName.getAbsoluteFile());
			} catch (IOException ioException) {
			}
		}
	}

	private void saveFile() {
		txtArea.requestFocus();
		if (isNewFile) {
			writerFile();
		} else {
			try {
				FileWriter fw = new FileWriter(currentFile);
				BufferedWriter bfw = new BufferedWriter(fw);
				bfw.write(txtArea.getText(), 0, txtArea.getText().length());
				bfw.flush();
				fw.close();
			} catch (IOException ioException) {
			}
		}
	}

	private void openFile() {
		txtArea.requestFocus();
		String currentValue = txtArea.getText();
		boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
		if (isTextChange) {
			int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存，是否保存？", "提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (saveChoose == JOptionPane.YES_OPTION) {
				writerFile();
			} else if (saveChoose == JOptionPane.NO_OPTION) {
				String str = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle("打开文件");
				int result = fileChooser.showOpenDialog(this);
				if (result == JFileChooser.CANCEL_OPTION) {
					statusLabel.setText("您没有选择任何文件");
					return;
				}
				File fileName = fileChooser.getSelectedFile();
				readerFile(fileName);
			} else {
				return;
			}
		} else {
			String str = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("打开文件");
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				statusLabel.setText(" 您没有选择任何文件 ");
				return;
			}
			File fileName = fileChooser.getSelectedFile();
			readerFile(fileName);
		}
	}

	private void readerFile(File fileName) {
		String str;
		if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileReader fr = new FileReader(fileName);
                BufferedReader bfr = new BufferedReader(fr);
                txtArea.setText("");
                while ((str = bfr.readLine()) != null) {
                    txtArea.append(str);
                }
                this.setTitle(fileName.getName() + " - 记事本");
                statusLabel.setText(" 当前打开文件：" + fileName.getAbsoluteFile());
                fr.close();
                isNewFile = false;
                currentFile = fileName;
                oldValue = txtArea.getText();
            } catch (IOException ioException) {
            }
        }
	}

	private void writerFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("另存为");
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) {
            statusLabel.setText("您没有选择任何文件");
            return;
        }
		File saveFileName = fileChooser.getSelectedFile();
		if (saveFileName == null || saveFileName.getName().equals("")) {
            JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileWriter fw = new FileWriter(saveFileName);
                BufferedWriter bfw = new BufferedWriter(fw);
                bfw.write(txtArea.getText(), 0, txtArea.getText().length());
                bfw.flush();// 刷新该流的缓冲
                bfw.close();
                isNewFile = false;
                currentFile = saveFileName;
                oldValue = txtArea.getText();
                this.setTitle(saveFileName.getName() + " - 记事本");
                statusLabel.setText("当前打开文件：" + saveFileName.getAbsoluteFile());
            } catch (IOException ioException) {
            }
        }
	}

	private void newFile() {
		txtArea.requestFocus();
		String currentValue = txtArea.getText();
		boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
		if (isTextChange) {
			int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存，是否保存？", "提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (saveChoose == JOptionPane.YES_OPTION) {
				writerFile();
			} else if (saveChoose == JOptionPane.NO_OPTION) {
				newFileToArea();
			} else if (saveChoose == JOptionPane.CANCEL_OPTION) {
				return;
			}
		} else {
			newFileToArea();
		}
	}

	private void newFileToArea() {
		txtArea.replaceRange("", 0, txtArea.getText().length());
		statusLabel.setText(" 新建文件");
		this.setTitle("无标题 - 记事本");
		isNewFile = true;
		undo.discardAllEdits();// 撤消所有的"撤消"操作
		editMenuUndo.setEnabled(false);
		oldValue = txtArea.getText();
	}

	// 实现接口UndoableEditListener的类UndoHandler(与撤销操作有关)
	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent uee) {
			undo.addEdit(uee.getEdit());
		}

	}

	// 实现DocumentListener接口中的方法(与撤销操作有关)
	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		editMenuUndo.setEnabled(true);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		editMenuUndo.setEnabled(true);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		editMenuUndo.setEnabled(true);
	}
}
