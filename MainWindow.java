

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.UIManager;

/**
 * @author Prasetyo Adhi Putranto (GUID: 2340000P)
 *
 */
public class MainWindow {

	private JFrame frmJavaBallMatches;
	private final String inputFile = "TeamsIn.txt";
	private final String resultFile = "ResultsIn.txt";
	private final String outputFile = "ResultsOut.txt";
	private JLabel lblMatchesList;
	private JTextArea textArea;
	private JLabel lblTeamList;
	private JList<Object> listTeam;
	private JButton btnWithdrawSelectedTeam;
	private ArrayList<String> lTeams = new ArrayList<String>();
	private ArrayList<String> lTeams2 = new ArrayList<String>();
	private JButton btnReadResultFile; 
	private JButton btnAddResult;
	private TreeMap<String, Match> matchTable = new TreeMap<String, Match>();
	private TreeMap<String, Team> teamTable = new TreeMap<String, Team>();
	private JLabel lbScore1;
	private JComboBox<String> cbScore1;
	private JComboBox<Match> cbMatch;
	private JLabel lbScore2;
	private JComboBox<String> cbScore2;
	private boolean isResultFileLoaded = false;
	private JButton btnProcessResult;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmJavaBallMatches.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		readInputFile();
	}

	/**
	 * Read input file, parse and show to the textarea
	 */
	private void readInputFile() {
		FileReader fReader;
		try {
			fReader = new FileReader(inputFile);
			BufferedReader bReader = new BufferedReader(fReader);
			String sLine = "";
			
			while ((sLine = bReader.readLine()) != null) {
				lTeams.add(sLine);
				//teamList.add(new Team(sLine));
				teamTable.put(sLine.toLowerCase(), new Team(sLine));
			}
			Collections.sort(lTeams);

			lTeams2.addAll(lTeams);
			
			for (String team : lTeams) {		
				for (String team2 : lTeams2) {
					if (! team.equals(team2)) {
						String matchKey = team + team2 + "|" + team2 + team;
						Match matchItem = new Match();
						matchItem.setTeam1(team);
						matchItem.setTeam2(team2);
						matchTable.put(matchKey, matchItem);
					}
				}
				lTeams2.remove(team);
			}
			
			updateDisplayMatchAndTeam();
			
			bReader.close();
			fReader.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frmJavaBallMatches, "Could not find file: " + inputFile, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int readResultFile() {
		FileReader fReader = null;
		int recordsUpdated = 0;
		
		try {
			fReader = new FileReader(resultFile);
			BufferedReader bReader = new BufferedReader(fReader);
			String lines = "";
			
			int score1, score2 = 0;
			String team1, team2 = "";
			
			while ((lines = bReader.readLine()) != null) {
				String[] splitBuff = lines.split(" ");
				String key2search = "";
				team1 = splitBuff[0];
				team2 = splitBuff[2];
				score1 = Integer.parseInt(splitBuff[1]);
				score2 = Integer.parseInt(splitBuff[3]);
				
				if (team1.compareTo(team2) < 0) {
					key2search = team1 + team2 + "|" + team2 + team1;
				} else {
					key2search = team2 + team1 + "|" + team1 + team2;
				}
				
				// update match table
				Match mitem = matchTable.get(key2search);
				if (mitem != null) {
					if (team1.compareTo(team1) < 0) {
						mitem.setTeam1Score(score1);
						mitem.setTeam2Score(score2);
					} else {
						mitem.setTeam1Score(score2);
						mitem.setTeam2Score(score1);						
					}
					mitem.setHasResult(true);
					matchTable.replace(key2search, mitem);
					recordsUpdated++;
				}		
				
				// update team table
				Team titem1 = teamTable.get(team1.toLowerCase());
				Team titem2 = teamTable.get(team2.toLowerCase());
				
				if ((titem1 != null) && (titem2 != null)) {
					if (score1 > score2) {
						// team1 is a winner
						titem1.won(score1, score2);
						titem2.lost(score2, score1);
					} else if (score1 == score2) {
						// match is drawn
						titem1.drawn(score1);
						titem2.drawn(score2);
					} else {
						// is a loser
						titem1.lost(score1, score2);
						titem2.won(score2, score1);
					}
					teamTable.replace(team1.toLowerCase(), titem1);
					teamTable.replace(team2.toLowerCase(), titem2);
				}
			}
			isResultFileLoaded = true;
			updateDisplayMatchAndTeam();
			
			bReader.close();
			fReader.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frmJavaBallMatches, "Could not find file: " + resultFile, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frmJavaBallMatches, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		return recordsUpdated;
	}

	private void updateDisplayMatchAndTeam() {
		if (! lTeams2.isEmpty()) {
			lTeams2.clear();
		}
		lTeams2.addAll(lTeams);
		listTeam.removeAll();
		listTeam.setListData(lTeams.toArray());
		
		textArea.setText("");
						
		String matchText = "";
		Match matchItem = null;
		int matches = 0;
		ArrayList<String> matchNameList = new ArrayList<String>();
		ArrayList<Match> noresultMatches = new ArrayList<Match>();
		
		
		for (Map.Entry<String, Match> match : matchTable.entrySet()) {
			matchItem = match.getValue();
			if (! matchItem.isHasResult()) {
				matchText = String.format("%1$-30s", matchItem.getTeam1() 
						+ "  v  " + matchItem.getTeam2()) 
						+ "*** no results yet ***"
						+ System.lineSeparator();
				matchNameList.add(matchItem.getMatchName());
				noresultMatches.add(matchItem);
			} else {
				matchText = String.format("%1$-10s", matchItem.getTeam1())
						+ " " + matchItem.getTeam1Score()
						+ "   "
						+ String.format("%1$-10s", matchItem.getTeam2())
						+ " " + matchItem.getTeam2Score()
						+ System.lineSeparator();
			}
			textArea.append(matchText);		
			matches++;
		}
		
		ComboBoxModel<Match> teamModel = new MatchComboBoxModel(noresultMatches);
		cbMatch.setModel(teamModel);

		if (isResultFileLoaded) {
			btnAddResult.setEnabled(true);
			cbMatch.setEnabled(true);
			cbScore1.setEnabled(true);
			cbScore2.setEnabled(true);
		}
		
		// enable process result and ranking only after all matches have a result
		if (cbMatch.getItemCount() == 0) {
			btnProcessResult.setEnabled(true);
			btnAddResult.setEnabled(false);
		}
		
		lblMatchesList.setText("Matches List (Total " + matches + " matches):");

	}

	private void withdrawSelectedTeam() {
		TreeMap<String, Match> tempTable = new TreeMap<String, Match>();
		tempTable.putAll(matchTable);
		int teamsRemoved = 0;
		int matchesRemoved = 0;
		
		for (Object item : listTeam.getSelectedValuesList()) {
			lTeams.remove(item);
			teamsRemoved++;
			
			// remove any match containing withdrawn team
			for (Map.Entry<String, Match> match : tempTable.entrySet()) {
				if (match.getKey().contains(item.toString())) {
					matchTable.remove(match.getKey());
					matchesRemoved++;
				}
			}
			
			// remove team from team table
			teamTable.remove(item.toString().toLowerCase());
		}
						
		if (lTeams.size() < 3) {
			JOptionPane.showMessageDialog(frmJavaBallMatches, "The tournament has been cancelled", "Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(-1);
		} else {
			String text2show = teamsRemoved + " teams withdrawn from matches"
					+ System.lineSeparator()
					+ matchesRemoved + " related matches deleted";
			JOptionPane.showMessageDialog(frmJavaBallMatches, text2show, "Info", JOptionPane.INFORMATION_MESSAGE);
		}
		
		updateDisplayMatchAndTeam();
	}

	protected void processMatchesResult() {
		int recordsUpdated = readResultFile();
		JOptionPane.showMessageDialog(frmJavaBallMatches, recordsUpdated + " records updated", "Info", JOptionPane.INFORMATION_MESSAGE);
		btnReadResultFile.setEnabled(false);
		btnWithdrawSelectedTeam.setEnabled(false);
	}

	protected void addMatchResult() {
		
		if (cbMatch.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(frmJavaBallMatches, "Please select match first !", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			Match matchItem = (Match) cbMatch.getSelectedItem();
			matchItem.setTeam1Score(Integer.parseInt(cbScore1.getSelectedItem().toString()));
			matchItem.setTeam2Score(Integer.parseInt(cbScore2.getSelectedItem().toString()));
			matchItem.setHasResult(true);
			matchTable.replace(matchItem.getKey2Update(), matchItem);
			
			// update team table
			Team titem1 = teamTable.get(matchItem.getTeam1().toLowerCase());
			Team titem2 = teamTable.get(matchItem.getTeam2().toLowerCase());
			int score1 = matchItem.getTeam1Score();
			int score2 = matchItem.getTeam2Score();
			
			if ((titem1 != null) && (titem2 != null)) {
				if (score1 > score2) {
					// team1 is a winner
					titem1.won(score1, score2);
					titem2.lost(score2, score1);
				} else if (score1 == score2) {
					// match is drawn
					titem1.drawn(score1);
					titem2.drawn(score2);
				} else {
					// is a loser
					titem1.lost(score1, score2);
					titem2.won(score2, score1);
				}
				teamTable.replace(matchItem.getTeam1().toLowerCase(), titem1);
				teamTable.replace(matchItem.getTeam2().toLowerCase(), titem2);
			}
			
		}
		
		updateDisplayMatchAndTeam();
		cbScore1.setSelectedIndex(0);
		cbScore2.setSelectedIndex(0);
	}

	protected void processResultAndRank() {
		
		textArea.setText("");
		
		String header1 = String.format("%1$-24s", "")
				+ String.format("%1$-20s", "Matches")
				+ String.format("%1$-13s", "Goals")
				+ String.format("%1$-11s", "Match")
				+ String.format("%1$-30s", "Goal");
				
		String header2 = String.format("%1$-14s", "Team")
				+ String.format("%1$-6s", "Rank")
				+ String.format("%1$-5s", "Won")
				+ String.format("%1$-7s", "Drawn")
				+ String.format("%1$-8s", "Lost")
				+ String.format("%1$-5s", "For")
				+ String.format("%1$-12s", "Against")
				+ String.format("%1$-11s", "Points")
				+ String.format("%1$-11s", "Diff")
				+ String.format("%1$-10s", "Medal");
		
		textArea.append(header1 + System.lineSeparator());
		textArea.append(header2 + System.lineSeparator());
				
		List<Team> tempTeam = new ArrayList<Team>();
		tempTeam.addAll(teamTable.values());
		
		// sort list using custom comparator
		Collections.sort(tempTeam, new TeamComparator());
		
		int orders = 0, prevPoint = 0, prevDiff = 0, rank = 0;
		String medal = "";
		
		for (Team item : tempTeam) {
			orders++;
			if (orders == 1) {
				medal = "Gold";
				rank = orders;
			} else {
				if ((prevPoint == item.getMatchPoint()) && (prevDiff == item.getGoalDifference())) { 
				} else {
					switch (medal) {
						case "Gold": 
							medal = "Silver";
							break;
						case "Silver": 
							medal = "Bronze";
							break;
						case "Bronze": 
							medal = "";
							break;
					}		
					rank = orders;
				}
			}
			prevPoint = item.getMatchPoint();
			prevDiff = item.getGoalDifference();
			
			String line = String.format("%1$-14s", item.getName())
					+ String.format("%1$3s", rank)
					+ String.format("%1$5s", item.getWon())
					+ String.format("%1$6s", item.getDrawn())
					+ String.format("%1$7s", item.getLost())
					+ String.format("%1$8s", item.getGoalFor())
					+ String.format("%1$7s", item.getGoalAgainst())
					+ String.format("%1$11s", item.getMatchPoint())
					+ String.format("%1$10s", item.getGoalDifference())
					+ "        "
					+ String.format("%1$-12s", medal);

			textArea.append(line + System.lineSeparator());
		}

	}

	protected void writeOutputAndExit() {
		
		try {
			FileWriter fwriter = new FileWriter(outputFile);
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(textArea.getText());
			bwriter.flush();
			bwriter.close();
			
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frmJavaBallMatches, e.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJavaBallMatches = new JFrame();
		frmJavaBallMatches.setTitle("Java Ball Matches");
		frmJavaBallMatches.setBounds(100, 100, 775, 684);
		frmJavaBallMatches.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		lblMatchesList = new JLabel("Matches List:");
		lblMatchesList.setFont(new Font("Tahoma", Font.BOLD, 16));
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		lblMatchesList.setLabelFor(textArea);
		textArea.setEditable(false);
		
		lblTeamList = new JLabel("Team List:");
		lblTeamList.setFont(new Font("Tahoma", Font.BOLD, 16));
		
		listTeam = new JList<>();
		lblTeamList.setLabelFor(listTeam);
		listTeam.setToolTipText("Please select one or more teams to withdraw");
		
		btnWithdrawSelectedTeam = new JButton("Withdraw selected team");
		btnWithdrawSelectedTeam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				withdrawSelectedTeam();
			}
		});
		
		btnReadResultFile = new JButton("Load Result File");
		btnReadResultFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processMatchesResult();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Choose match to add result:");
		
		cbMatch = new JComboBox<Match>();
		cbMatch.setEnabled(false);
		cbMatch.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Match selectedMatch = (Match) e.getItem();
				lbScore1.setText(selectedMatch.getTeam1() + " score:");
				lbScore2.setText(selectedMatch.getTeam2() + " score:");
			}
		});
		lblNewLabel.setLabelFor(cbMatch);
		cbMatch.setMaximumRowCount(10);
		
		lbScore1 = new JLabel("Team-1 Score:");
		
		cbScore1 = new JComboBox<String>();
		cbScore1.setEnabled(false);
		cbScore2 = new JComboBox<String>();
		cbScore2.setEnabled(false);
		cbScore2.setModel(new DefaultComboBoxModel<String>(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));

		lbScore1.setLabelFor(cbScore1);
		cbScore1.setModel(new DefaultComboBoxModel<String>(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));
		
		lbScore2 = new JLabel("Team-2 Score:");
			
		JButton btnClearSelection = new JButton("Clear selection");
		btnClearSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listTeam.clearSelection();
			}
		});
		
		btnAddResult = new JButton("Add Match Result");
		btnAddResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMatchResult();
			}
		});
		btnAddResult.setEnabled(false);
		
		btnProcessResult = new JButton("Process Result & Create Rank");
		btnProcessResult.setEnabled(false);
		btnProcessResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processResultAndRank();
			}
		});
		
		JButton btnExit = new JButton("Close & Exit");
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeOutputAndExit();
			}
		});
		btnExit.setBackground(UIManager.getColor("Button.background"));
		
		GroupLayout groupLayout = new GroupLayout(frmJavaBallMatches.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblMatchesList)
						.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnProcessResult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnReadResultFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnWithdrawSelectedTeam)
							.addComponent(lblNewLabel)
							.addComponent(lblTeamList)
							.addComponent(btnClearSelection)
							.addComponent(btnAddResult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(cbMatch, Alignment.TRAILING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(listTeam, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbScore1)
									.addComponent(cbScore1, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addComponent(cbScore2, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbScore2))))
						.addComponent(btnExit))
					.addGap(20))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblMatchesList)
						.addComponent(lblTeamList))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 560, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(listTeam, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(btnWithdrawSelectedTeam)
							.addGap(4)
							.addComponent(btnClearSelection)
							.addGap(32)
							.addComponent(lblNewLabel)
							.addGap(8)
							.addComponent(cbMatch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lbScore1)
								.addComponent(lbScore2))
							.addGap(2)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(cbScore1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(cbScore2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(14)
							.addComponent(btnAddResult)
							.addGap(7)
							.addComponent(btnReadResultFile)
							.addGap(12)
							.addComponent(btnProcessResult)
							.addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
							.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addGap(5)))
					.addGap(130))
		);
		frmJavaBallMatches.getContentPane().setLayout(groupLayout);
	}
}
