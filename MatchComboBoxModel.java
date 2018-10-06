/**
 * @author Prasetyo Adhi Putranto (GUID: 2340000P)
 *
 */

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;;

public class MatchComboBoxModel implements ComboBoxModel<Match> {
	
	private ArrayList<Match> matchPool;
	private Match selectedMatch;

	public MatchComboBoxModel(ArrayList<Match> matchList) {
		matchPool = new ArrayList<Match>();
		this.matchPool.addAll(matchList);
	}
	
	public int getSize() {
		return this.matchPool.size();
	}

	public Match getElementAt(int index) {
		return this.matchPool.get(index);
	}

	public void addListDataListener(ListDataListener l) {}

	public void removeListDataListener(ListDataListener l) {}

	public void setSelectedItem(Object anItem) {
		this.selectedMatch = (Match) anItem;		
	}

	public Object getSelectedItem() {
		return this.selectedMatch;
	}

}
