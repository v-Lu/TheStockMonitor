package victorluproductions.stockpile.Views;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import victorluproductions.stockpile.Helpers.DateHandler;
import victorluproductions.stockpile.Fragments.DatePickerFragment;
import victorluproductions.stockpile.R;

public class MainActivity extends FragmentActivity
						  implements DatePickerFragment.OnDateSetListener {
	//retrofit tag
	private final static String TAG = StockSearchResultActivity.class.getSimpleName();

	@InjectView(R.id.start_date)
	protected EditText startDate;

	@InjectView(R.id.end_date)
	protected EditText endDate;

	@InjectView(R.id.ticker_symbol)
	protected EditText ticker;

	@InjectView(R.id.search)
	protected Button searchButton;

	@InjectView(R.id.open_checkbox)
	protected CheckBox openCheckbox;

	@InjectView(R.id.high_checkbox)
	protected CheckBox highCheckbox;

	@InjectView(R.id.low_checkbox)
	protected CheckBox lowCheckbox;

	@InjectView(R.id.close_checkbox)
	protected CheckBox closeCheckbox;

	protected int startDateId;
	protected int endDateId;
	protected ArrayList<String> yahooResults = new ArrayList<String>();
	protected ArrayList<String> graphX = new ArrayList<String>();
	protected ArrayList<String> graphY = new ArrayList<String>();
	protected ArrayList<String> newsTitles  = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.inject(this);

		startDateId = startDate.getId();
		endDateId = endDate.getId();

		// setup date pickers
		setDatePickerOnClickListener(startDate);
		setDatePickerOnClickListener(endDate);
		setSearchButtonOnClickListener(searchButton);
	}

	public void setSearchButtonOnClickListener(Button searchButton)	{
		searchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// validate that ticker symbol and date fields are populated
				if (!validFields())
					return;

				Intent intent = new Intent(MainActivity.this, StockSearchResultActivity.class);

				intent.putExtra("ticker", ticker.getText());
				intent.putExtra("startDate", startDate.getText());
				intent.putExtra("endDate", endDate.getText());
				intent.putExtra("openCheckbox", openCheckbox.isChecked());
				intent.putExtra("highCheckbox", highCheckbox.isChecked());
				intent.putExtra("lowCheckbox", lowCheckbox.isChecked());
				intent.putExtra("closeCheckbox", closeCheckbox.isChecked());
				startActivity(intent);

				/**
				RestClient rc = new RestClient();

				// find news based on ticker symbol (somewhat buggy for symbols like 'DATA')
				String yql = "select * from google.news where q =\"" + ticker.getText() + "\"";

				rc.getYahooApiService().getStockNews(yql,
						new Callback<NewsQuery>()
						{
							@Override
							public void success(NewsQuery results, Response response) {

								newsTitles.clear();

								// parse news results
								if (results.getQuery().getResults() != null) {
									List<Result> queryResults = results.getQuery().getResults().getResults();

									for (Result r : queryResults) {
										for (RelatedStory rs : r.getRelatedStories()) {
											newsTitles.add(rs.getTitleNoFormatting().toString());
										}
										newsTitles.add(r.getTitleNoFormatting());
									}
								}
							}

							@Override
							public void failure(RetrofitError error)
							{
								Log.e(TAG, "Error : " + error.getMessage());
							}
						});

				// get ticker symbol's historical data for date range
				yql = "select * from yahoo.finance.historicaldata where symbol =\"{0}\" and startDate = \"{1}\" and endDate = \"{2}\"";
				MessageFormat mf = new MessageFormat(yql);
				yql = mf.format(yql, ticker.getText(), startDate.getText(), endDate.getText());

				rc.getYahooApiService().getStockHistoricalData(yql,
						new Callback<HistoricalDataQuery>()
						{
							@Override
							public void success(HistoricalDataQuery results, Response response)
							{
								yahooResults.clear();
								graphX.clear();
								graphY.clear();

								// parse historical data results
								if (results.getQuery().getResults() != null)
								{
									List<Quote> q = results.getQuery().getResults().getResults();

									for(Quote quote : q) {

										String output = quote.getDate() + ": ";

										if (openCheckbox.isChecked())
											output += "Open[" + quote.getOpen() + "], ";

										if (highCheckbox.isChecked())
											output += "High[" + quote.getHigh() + "], ";

										if (lowCheckbox.isChecked())
											output += "Low[" + quote.getLow() + "], ";

										if (closeCheckbox.isChecked())
											output += "Close[" + quote.getClose() + "], ";

										output = output.replaceAll(",$", "");
										yahooResults.add(output);

										graphX.add(quote.getDate());
										graphY.add(String.valueOf(quote.getOpen()));
									}
								}
								if (!yahooResults.isEmpty())
								{
									Intent intent = new Intent(MainActivity.this, StockSearchResultActivity.class);
									intent.putStringArrayListExtra("results", yahooResults);
									intent.putStringArrayListExtra("graphX", graphX);
									intent.putStringArrayListExtra("graphY", graphY);
									intent.putStringArrayListExtra("newsTitles", newsTitles);
									intent.putExtra("ticker", ticker.getText());

									startActivity(intent);
								} else {
									final AlertDialog.Builder noResultDialog  = new AlertDialog.Builder(MainActivity.this);
									noResultDialog.setTitle("No Results");
									noResultDialog.setMessage("Blah... Try again!");
									noResultDialog.setPositiveButton("Ok",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											});
									noResultDialog.show();
								}
							}
							@Override
							public void failure(RetrofitError error)
							{
								Log.e(TAG, "Error : " + error.getMessage());
							}
						}); **/
			}
		});
	}

	public void setDatePickerOnClickListener (EditText textBox) {
		textBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment datePicker = new DatePickerFragment();
				int currYear;
				int currMonth;
				int currDay;
				EditText et = (EditText) v;

				String textBoxValue = et.getText().toString();

				if (textBoxValue.isEmpty()) {
					Calendar c = Calendar.getInstance();
					currYear = c.get(Calendar.YEAR);
					currMonth = c.get(Calendar.MONTH);
					currDay = c.get(Calendar.DAY_OF_MONTH);

					datePicker = DatePickerFragment.newInstance(currYear, currMonth, currDay, et.getId());
				} else {
					DateHandler dateService = new DateHandler();
					Calendar c = dateService.parseDate(textBoxValue);

					currYear = c.get(Calendar.YEAR);
					currMonth = c.get(Calendar.MONTH);
					currDay = c.get(Calendar.DAY_OF_MONTH);

					datePicker = DatePickerFragment.newInstance(currYear, currMonth, currDay, et.getId());
				}
				datePicker.show(getFragmentManager(), "datePicker");
			}
		});
	}

	public void OnDateSelected(String date, int dateTextBoxId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date();
		try {
			dt = dateFormat.parse(date);
			date = dateFormat.format(dt);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		if (dateTextBoxId == startDateId) {
			startDate.setText(date);
		}
		else if (dateTextBoxId == endDateId) {
			endDate.setText(date);
		}
	}

	public boolean validFields() {

		if (ticker.getText().toString().isEmpty())
			return false;

		if (startDate.getText().toString().isEmpty())
			return false;

		if (endDate.getText().toString().isEmpty())
			return false;

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}























