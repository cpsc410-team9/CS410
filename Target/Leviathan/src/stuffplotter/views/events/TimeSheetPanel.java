package stuffplotter.views.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import stuffplotter.views.util.DateSplitter;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to display the view of the proposed times for an event.
 */
public class TimeSheetPanel extends SimplePanel
{
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "480px";
	private ScrollPanel timeSheetWindow;
	private HorizontalPanel horPanel;
	
	/**
	 * Constructor for TimeSheetPanel.
	 * @pre true;
	 * @post this.isVisible() == true;
	 */
	public TimeSheetPanel()
	{
		super();
		timeSheetWindow = new ScrollPanel();
		timeSheetWindow.setSize(WIDTH, HEIGHT);
		horPanel = new HorizontalPanel();
		timeSheetWindow.add(horPanel);
		this.add(timeSheetWindow);
	}

	/**
	 * Method to add a new day with all time slots to the time sheet, a month panel will be
	 * generated where appropriate.
	 * @pre date != null;
	 * @post true; 
	 * @param date - the list of Dates to display.
	 * @param conflictDates - the list of time slots that are already taken.
	 */
	public void addDay(Date date, List<Date> conflictDates)
	{
		DateSplitter splitter = new DateSplitter(date);
		int month = splitter.getMonth();
		int year = splitter.getYear();
		
		boolean monthYearFound = false;
		int numOfMonthPanels = this.horPanel.getWidgetCount();
		int i = 0;
		
		// while loop to determine if the month is already in the time sheet panel
		while(!monthYearFound && i < numOfMonthPanels)
		{
			Widget childWidget = this.horPanel.getWidget(i); 
			if(childWidget instanceof MonthPanel)
			{
				MonthPanel monthPanel = (MonthPanel) childWidget;
				if(monthPanel.getMonthValue() == month && monthPanel.getYearValue() == year)
				{
					((MonthPanel) childWidget).addDay(date, conflictDates);
					monthYearFound = true;
				}
			}
			i++;
		}
		
		if(!monthYearFound)
		{
			boolean locationFound = false;
			int j = 0;
			
			// while loop to find the correct place to add the month panel
			while(!locationFound && j < numOfMonthPanels)
			{
				Widget childWidget = this.horPanel.getWidget(j); 
				if(childWidget instanceof MonthPanel)
				{
					MonthPanel monthPanel = (MonthPanel) childWidget;
					if(year < monthPanel.getYearValue())
					{
						locationFound = true;
						this.horPanel.insert(new MonthPanel(date, conflictDates), j);
					}
					else if(year == monthPanel.getYearValue())
					{
						if(month < monthPanel.getMonthValue())
						{
							locationFound = true;
							this.horPanel.insert(new MonthPanel(date, conflictDates), j);
						}
					}
				}
				
				j++;
			}
			
			if(!locationFound)
			{
				this.horPanel.add(new MonthPanel(date, conflictDates));
			}
		}
	}
	
	/**
	 * Method to add the given list of days contained in the list of Dates.
	 * @pre dates != null && !dates.isEmpty();
	 * @post true; 
	 * @param dates - the list of Dates to display.
	 * @param conflictDates - the list of time slots that are already taken.
	 */
	public void addDays(List<Date> dates, List<Date> conflictDates)
	{
		HashMap<Integer, List<Date>> hashMap = new HashMap<Integer, List<Date>>();
		List<List<Integer>> monthYears = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < dates.size(); i++)
		{
			DateSplitter splitter = new DateSplitter(dates.get(i));
			List<Integer> monthYear = new ArrayList<Integer>(2);
			monthYear.add(splitter.getMonth());
			monthYear.add(splitter.getYear());
			int key = monthYear.get(0) + monthYear.get(1);
			
			if(!monthYears.contains(monthYear))
			{
				List<Date> monthYearDates = new ArrayList<Date>();
				
				monthYears.add(monthYear);
				monthYearDates.add(dates.get(i));
				
				hashMap.put(key, monthYearDates);
			}
			else
			{
				List<Date> monthYearDates = hashMap.get(key);
				monthYearDates.add(dates.get(i));
				hashMap.put(key, monthYearDates);
			}	
		}

		//System.out.println("monthYears.size() = " + monthYears.size());
		
		for (List<Date> monthYearDates : hashMap.values())
		{
			this.horPanel.add(new MonthPanel(monthYearDates, conflictDates));
		}
	}
	
	/**
	 * Method to retrieve the submission made by the user for the event.
	 * @pre true;
	 * @post true;
	 * @return the submission made by the user for the event.
	 */
	public List<Date> retrieveSubmission()
	{
		List<Date> selectedValues = new ArrayList<Date>();
		
		// for loop to get the submission information from each MonthPanel
		for (int i = 0; i < this.horPanel.getWidgetCount(); i++)
		{
			Widget childWidget = this.horPanel.getWidget(i); 
			if(childWidget instanceof MonthPanel)
			{
				selectedValues.addAll(((MonthPanel) childWidget).retrieveSubmission());
			}
		}
		
		return selectedValues;
	}
}
