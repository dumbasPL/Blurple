package me.dumbasPL.blurple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends JFrame {

	public static final long serialVersionUID = 1981976985361858036L;
	public JPanel contentPane;
	public JTextField filetextbox;

	public BufferedImage bi;
	public JPanel prev;
	public JCheckBox invertCB;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		setTitle("Blurple by dumbasPL");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 756, 655);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblFile = new JLabel("file: ");
		panel.add(lblFile, BorderLayout.WEST);

		filetextbox = new JTextField();
		filetextbox.setEditable(false);
		panel.add(filetextbox);
		filetextbox.setColumns(60);

		JButton btnSelect = new JButton("select");
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					try {
						bi = ImageIO.read(f);
						filetextbox.setText(f.getAbsolutePath());
						prev.repaint();
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(Main.this, "Unable to load file", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		panel.add(btnSelect, BorderLayout.EAST);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblSensitivity = new JLabel("sensitivity: ");
		panel_1.add(lblSensitivity, BorderLayout.WEST);

		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JSlider) {
					prev.repaint();
				}
			}
		});
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		panel_1.add(slider, BorderLayout.CENTER);

		JButton btnSaveAs = new JButton("save as");
		btnSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage b = applyBlue(copyImage(bi), slider.getValue());
				JFileChooser fc = new JFileChooser();
				if (fc.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					if (!f.getName().endsWith(".png")) {
						f = new File(f.getAbsolutePath() + ".png");
					}
					try {
						ImageIO.write(b, "png", f);
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(Main.this, "Unable to save file", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		panel_1.add(btnSaveAs, BorderLayout.EAST);

		invertCB = new JCheckBox("invert");
		invertCB.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				prev.repaint();
			}
		});
		invertCB.setHorizontalAlignment(SwingConstants.CENTER);
		invertCB.setFont(new Font("Tahoma", Font.PLAIN, 11));
		panel_1.add(invertCB, BorderLayout.SOUTH);

		prev = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				updateImage(slider.getValue(), g);
			}
		};
		contentPane.add(prev, BorderLayout.CENTER);
	}

	public void updateImage(int val, Graphics g) {
		if (bi == null)
			return;
		int w = prev.getWidth();
		int h = prev.getHeight();
		int iw = bi.getWidth();
		int ih = bi.getHeight();
		double scalex = (double) w / iw;
		double scaley = (double) h / ih;
		double scale = Math.min(scalex, scaley);
		int ww = (int) (iw * scale);
		int hh = (int) (ih * scale);

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g.clearRect(0, 0, w, h);
		g.drawImage(applyBlue(copyImage(bi), val), 0, 0, ww, hh, null);
		prev.getGraphics().drawImage(img, 0, 0, null);
	}

	public BufferedImage applyBlue(BufferedImage i, int p) {
		Color blurple = new Color(114, 137, 218);
		Color white = new Color(255, 255, 255);
		int val = map(p, 0, 100, 0, 255);
		int[] data = i.getRGB(0, 0, i.getWidth(), i.getHeight(), null, 0, i.getWidth());
		for (int j = 0; j < data.length; j++) {
			int color = data[j];
			int blue = color & 0xff;
			int green = (color & 0xff00) >> 8;
			int red = (color & 0xff0000) >> 16;
			int brightness = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
			data[j] = brightness <= val ^ invertCB.isSelected() ? blurple.getRGB() : white.getRGB();
		}
		i.setRGB(0, 0, i.getWidth(), i.getHeight(), data, 0, i.getWidth());
		return i;
	}

	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	public static int map(float OldValue, float OldMin, float OldMax, float NewMin, float NewMax) {
		return (int) ((((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin);
	}

}
