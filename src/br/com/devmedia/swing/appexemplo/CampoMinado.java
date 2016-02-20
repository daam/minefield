/*
 * Aplicação de exemplo sobre a API Java Swing para a revista JavaMagazine
 * Todos os direitos reservados.
 * 
 * @ano 2012
 * @author Daniel A A Mascena
 * @email danielmascena@gmail.com
 */
package br.com.devmedia.swing.appexemplo;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CampoMinado extends JFrame {

	enum Nivel {
		INICIANTE, INTERMEDIARIO, AVANCADO
	}

	private static final long serialVersionUID = 1L;
	private final ImageIcon blank = createImageIcon("blank.gif");
	private final ImageIcon bomb = createImageIcon("bombrevealed.png");
	private MouseAdapter mouseListener = new MouseAdapter() {
		public void mouseReleased(MouseEvent e) {
			if (e.getModifiers() == InputEvent.BUTTON1_MASK)
				verificarPonto(e);
		}
	};
	private JPanel grid;
	private JFrame janelaOpcoes;
	private Nivel nivelJogo;
	private Nivel nivelTempOpcoes;
	private Ponto[] campo;
	private int totalSemMinas;
	private int cols;
	private int lins;
	private int numeroMinas;
	private int quantidadePercorrida = 0;
	private ArrayList<Integer> localMinas;

	public CampoMinado() {

		setJMenuBar(criarMenuBar());
		setTitle("Campo Minado");
		setIconImage(createImageIcon("Minesweeper_Icon.png").getImage());
		nivelJogo = nivelTempOpcoes = Nivel.INICIANTE;
		criarPainelJogo();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JMenuBar criarMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Jogo");
		JMenu sobre = new JMenu("Sobre");
		menuBar.add(menu);
		menuBar.add(sobre);
		JMenuItem novoJogo = new JMenuItem("Novo Jogo", KeyEvent.VK_J);
		novoJogo.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				criarNovoJogo();
			}
		});
		JMenuItem opcoes = new JMenuItem("Opções", KeyEvent.VK_O);
		opcoes.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (janelaOpcoes == null)
					iniciarJanelaOpcoes();
				janelaOpcoes.setVisible(true);
			}
		});
		JMenuItem sair = new JMenuItem("Sair", KeyEvent.VK_S);
		opcoes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.ALT_MASK));
		sair.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.exit(0);
			}
		});
		menu.add(novoJogo);
		menu.add(opcoes);
		menu.add(sair);
		sobre.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"Implementação do jogo Campo Minado utilizando \na API Swing para a revista JavaMagazine."
										+ "\n\nDúvidas podem ser encaminhadas para:\ndanielmascena@gmail.com,
								"Sobre", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		return menuBar;
	}

	private void iniciarJogoPreferencias() {
		switch (nivelJogo) {
		case INICIANTE:
			cols = 10;
			lins = 10;
			numeroMinas = 10;
			break;
		case INTERMEDIARIO:
			cols = 17;
			lins = 17;
			numeroMinas = 40;
			break;
		case AVANCADO:
			cols = 31;
			lins = 17;
			numeroMinas = 99;
			break;
		}
		quantidadePercorrida = 0;
	}

	private void criarNovoJogo() {
		grid.removeAll();
		iniciarJogoPreferencias();
		grid.setLayout(new GridLayout(lins, cols));
		criarCampo();
		grid.setEnabled(true);
		setSize(getPreferredSize());
		grid.repaint();
		grid.validate();
	}

	private void criarPainelJogo() {
		iniciarJogoPreferencias();
		GridLayout layout = new GridLayout(lins, cols);
		layout.setHgap(0);
		layout.setVgap(0);
		grid = new JPanel(layout);
		criarCampo();
		JPanel area = new JPanel(new BorderLayout());
		area.add(grid);
		area.setBorder(new EmptyBorder(new Insets(30, 30, 30, 30)));
		add(area);
	}

	private void iniciarJanelaOpcoes() {
		janelaOpcoes = new JFrame();
		JPanel painel = new JPanel();
		BorderLayout layout = new BorderLayout();
		layout.layoutContainer(painel);

		final JRadioButton inicianteButton = new JRadioButton(
				"Iniciante (grade 9 x 9)");
		inicianteButton.setName("1");
		inicianteButton.setMnemonic(KeyEvent.VK_I);
		inicianteButton.setSelected(true);

		final JRadioButton intermediarioButton = new JRadioButton(
				"Intermediário (grade 16 x 16)");
		intermediarioButton.setName("2");
		intermediarioButton.setMnemonic(KeyEvent.VK_T);

		final JRadioButton avancadoButton = new JRadioButton(
				"Avançado (grade 16 x 30)");
		avancadoButton.setName("3");
		avancadoButton.setMnemonic(KeyEvent.VK_A);

		final ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(inicianteButton);
		radioGroup.add(intermediarioButton);
		radioGroup.add(avancadoButton);

		MouseListener acao = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String nivel = ((JRadioButton) e.getSource()).getName();
				nivelTempOpcoes = (nivel == "1") ? Nivel.INICIANTE
						: (nivel == "2") ? Nivel.INTERMEDIARIO : Nivel.AVANCADO;
			}
		};
		inicianteButton.addMouseListener(acao);
		intermediarioButton.addMouseListener(acao);
		avancadoButton.addMouseListener(acao);

		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(inicianteButton);
		radioPanel.add(intermediarioButton);
		radioPanel.add(avancadoButton);

		painel.add(new JLabel("Dificuldade:"), BorderLayout.LINE_START);
		painel.add(radioPanel, BorderLayout.CENTER);
		painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JButton confirmar = new JButton("Confirmar");
		JButton cancelar = new JButton("Cancelar");
		confirmar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (nivelJogo != nivelTempOpcoes) {
					nivelJogo = nivelTempOpcoes;
					criarNovoJogo();
				}
				janelaOpcoes.setVisible(false);
			}
		});
		cancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				janelaOpcoes.setVisible(false);
			}
		});
		ButtonGroup actionGroup = new ButtonGroup();
		actionGroup.add(confirmar);
		actionGroup.add(cancelar);

		painel.add(confirmar, BorderLayout.LINE_END);
		painel.add(cancelar, BorderLayout.LINE_END);

		janelaOpcoes.add(painel);
		janelaOpcoes.setTitle("Opções");
		janelaOpcoes.setSize(300, 200);
		janelaOpcoes.setResizable(false);
		janelaOpcoes.setAlwaysOnTop(true);
		janelaOpcoes.setLocationRelativeTo(null);
	}

	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = CampoMinado.class.getResource("/imagens/" + path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Arquivo não encontrado!");
			return null;
		}
	}

	private void verificarPonto(MouseEvent e) {
		Ponto p = (Ponto) e.getSource();
		if (localMinas.contains(p.obterPosicao())) {
			for (Integer i : localMinas) {
				Ponto bomba = (Ponto) campo[i];
				bomba.setEnabled(false);
				bomba.setDisabledIcon(bomb);
				bomba.removeMouseListener(mouseListener);
			}
			JOptionPane.showMessageDialog(null,
					"Você perdeu!\nInicie um novo jogo", "Derrota",
					JOptionPane.ERROR_MESSAGE);

			for (int i = 0; i < campo.length; i ++) {
				if ((p = campo[i]).isEnabled())
					p.setEnabled(false);
			}
			grid.setEnabled(false);
		} else {
			checarArea(p);
			if (quantidadePercorrida == totalSemMinas) {
				JOptionPane.showMessageDialog(null,
						"Você ganhou!\nInicie um novo jogo", "Vitória",
						JOptionPane.DEFAULT_OPTION);
				criarNovoJogo();
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// UIManager.put("swing.boldMetal", Boolean.FALSE);
				CampoMinado jogo = new CampoMinado();
				jogo.pack();
				jogo.setVisible(true);
				jogo.setLocationRelativeTo(null);
			}
		});
	}

	private void criarCampo() {
		campo = new Ponto[cols * lins];
		totalSemMinas = lins * cols - numeroMinas;
		localMinas = new ArrayList<Integer>(numeroMinas);
		int cont = 0;
		while (cont < numeroMinas) {
			int c = (int) Math.floor(Math.random() * cols);
			int l = (int) Math.floor(Math.random() * lins);
			if (!localMinas.contains(c + l * cols)) {
				localMinas.add(c + l * cols);
				cont++;
			}
		}
		Ponto ponto;
		for (int i = 0; i < lins; i++) {
			for (int j = 0; j < cols; j++) {
				ponto = new Ponto(i, j);
				ponto.addMouseListener(mouseListener);
				campo[i * cols + j] = ponto;
				grid.add(ponto);
			}
		}
	}

	private boolean estaContido(int x, int y) {
		return x >= 0 && x < lins && y >= 0 && y < cols;
	}

	private void checarArea(Ponto p) {

		if (p.isEnabled()) {
			int cont = 0;
			if (p.ehMina())
				return;

			for (int i = -1, l; i <= 1; i++) {
				l = p.getLinha() + i;
				for (int j = -1, c; j <= 1; j++) {
					c = p.getColuna() + j;
					if (estaContido(l, c))
						if (localMinas.contains(c + l * cols))
							cont++;
				}
			}
			++quantidadePercorrida;
			if (cont == 0) {
				p.setEnabled(false);
				p.setDisabledIcon(retornarOpenIcon(cont));
				for (int i = -1, l; i <= 1; i++) {
					l = p.getLinha() + i;
					for (int j = -1, c; j <= 1; j++) {
						c = p.getColuna() + j;
						if (estaContido(l, c))
							checarArea(campo[c + l * cols]);
					}
				}
			} else {
				p.setEnabled(false);
				p.setDisabledIcon(retornarOpenIcon(cont));
				return;
			}
		}
	}

	private ImageIcon retornarOpenIcon(int num) {
		return createImageIcon("open" + num + ".gif");
	}

	/**
	 * @author daniel.mascena
	 * 
	 */
	class Ponto extends JLabel {

		private static final long serialVersionUID = 1L;
		private int linha;
		private int coluna;

		Ponto(final int linha, final int coluna) {
			super(blank);
			this.linha = linha;
			this.coluna = coluna;
		}

		public int obterPosicao() {
			return this.coluna + this.linha * cols;
		}

		public boolean ehMina() {
			return localMinas.contains(obterPosicao());
		}

		public int getLinha() {
			return this.linha;
		}

		public int getColuna() {
			return this.coluna;
		}
	}

}