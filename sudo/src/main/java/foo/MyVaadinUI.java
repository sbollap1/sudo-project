package foo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("sudotheme")
@Push(PushMode.AUTOMATIC)
public class MyVaadinUI extends UI {

	private Container container = new IndexedContainer();
	private final VerticalLayout layout = new VerticalLayout();
	private Table table = new Table();
	private Label title = new Label("Shan's Sudoku Solver");
	private static UploadReceiver uploadReceiver = new UploadReceiver();
	private Upload upload = new Upload("Upload the file here", uploadReceiver);
	private Button solveButton = new Button("Solve");
	private Label statusLabel = new Label();
	private static Cube c1, c2, c3, c4, c5, c6, c7, c8, c9;
	private static List<Cube> cubeList;
	private Button populate = new Button("Populate Grid");
	private Label solvedSuccessfullyLabel = new Label("Puzzle Result Status");
	private Thread solverThread;

	@Override
	protected void init(VaadinRequest request) {

		upload.setImmediate(true);
		upload.setButtonCaption("Upload Sudoko file");

		title.addStyleName("mystyle");
		table.addStyleName("no-stripes");
		table.addStyleName("mymodel");

		createContainerData();
		table.setPageLength(0);
		table.setHeight(null);
		table.setContainerDataSource(container);
		table.setImmediate(true);
		table.setSelectable(false);
		table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);

		// Item item = container.getItem(9);
		// Property<Integer> nameProperty = item.getItemProperty(9);
		// nameProperty.setValue(99);
		// Notification.show("Clicked " + uploadReceiver.getMatrix()[7][8]);

		layout.addComponent(title);
		layout.addComponent(upload);
		layout.addComponent(statusLabel);
		layout.addComponent(populate);
		layout.addComponent(table);
		layout.addComponent(solveButton);
		layout.addComponent(solvedSuccessfullyLabel);

		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		// upload.setReceiver(uploadReceiver);
		upload.addFinishedListener(uploadReceiver);

		/*
		 * Click on the populate button
		 */
		populate.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				// statusLabel.setCaption(uploadReceiver.getFileName());
				solveButton.setEnabled(true);
				Object matrix[][] = uploadReceiver.getMatrix();
				int id = 0;
				for (Object[] row : matrix) {
					Item newItem = container.getItem(id);
					id++;
					// Item newItem = container.getItem(container.addItem());
					for (int i = 0; i < 9; i++) {
						newItem.getItemProperty(i).setValue(row[i]);
						table.setContainerDataSource(table.getContainerDataSource());
						table.setImmediate(true);
					}
				}

			}
		});

		/*
		 * Click on the Solve Button
		 */
		solveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				cubeList = new ArrayList<Cube>();
				c1 = createCube(CubeTypes.TOPLEFT_11);
				c2 = createCube(CubeTypes.TOPMIDDLE_12);
				c3 = createCube(CubeTypes.TOPRIGHT_13);
				c4 = createCube(CubeTypes.MIDDLELEFT_21);
				c5 = createCube(CubeTypes.MIDDLEMIDDLE_22);
				c6 = createCube(CubeTypes.MIDDLERIGHT_23);
				c7 = createCube(CubeTypes.BOTTOMLEFT_31);
				c8 = createCube(CubeTypes.BOTTOMMIDDLE_32);
				c9 = createCube(CubeTypes.BOTTOMRIGHT_33);
				cubeList.add(c1);
				cubeList.add(c2);
				cubeList.add(c3);
				cubeList.add(c4);
				cubeList.add(c5);
				cubeList.add(c6);
				cubeList.add(c7);
				cubeList.add(c8);
				cubeList.add(c9);
				solveButton.setEnabled(false);
				solverThread = new SolverThread();
				solverThread.start();
				System.out.println("Back from after spawning a thread. . .");

			}
		});

	}

	/************************/
	/* End of init function */
	/************************/

	/************************/
	/* Solver Thread */
	/************************/

	class SolverThread extends Thread {

		@Override
		public void run() {
			boolean flag = false;
			// This function is a recursive function
			try {
				solvedSuccessfullyLabel.setValue("Solving the puzzle. . .");
				flag = solveSudoko();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (flag && !solverThread.isAlive()) {
					solvedSuccessfullyLabel.setValue("Solved the puzzle !!! Hooray !!");
					solveButton.setEnabled(true);
				}
			}
		}

		/*
		 * Adding a runnable class
		 */
		class myRunnableClass implements Runnable {

			private int number;
			private Square celll;

			public myRunnableClass(int num, Square cell) {
				this.celll = cell;
				this.number = num;
			}

			@Override
			public void run() {
				final Item row = table.getItem(celll.getRowIndex());
				final Property<Integer> col = row.getItemProperty(celll.getColumnIndex());
				col.setValue(number);
				table.setCellStyleGenerator(new CellStyleGenerator() {

					@Override
					public String getStyle(Table source, Object itemId, Object propertyId) {
						// TODO Auto-generated method stub
						if (itemId == celll.getRowIndex() && propertyId == celll.getColumnIndex()) {
							return "mystylebhai";
						}
						return null;

					}
				});
				table.setContainerDataSource(table.getContainerDataSource());
			}

		}

		/*****************************/
		/* Solver Recursive function */
		/**
		 * @throws InterruptedException
		 ***************************/
		private boolean solveSudoko() throws InterruptedException {

			final Square cell = EmptySquarePresent();
			if (!cell.isEmptySquare()) {
				return true;

			}

			/*
			 * If there is empty square then only you will fall here; Reason why
			 * its 1 to 9 is because you want to try numbers from 1 to 9 in each
			 * cell
			 */
			for (int num = 1; num <= 9; num++) {

				// check if no conflicts then
				if (!AreThereConflicts(num, cell.getRowIndex(), cell.getColumnIndex())) {
					// assign a number to the cell if there are no conflicts
					uploadReceiver.getMatrix()[cell.getRowIndex()][cell.getColumnIndex()] = num;
					solvedSuccessfullyLabel.setValue("Empty Cell is (" + cell.getRowIndex() + "," + cell.getColumnIndex() + "): " + num);

					// Item row = table.getItem(cell.getRowIndex());
					// Property<Integer> col =
					// row.getItemProperty(cell.getColumnIndex());
					// col.setValue(num);

					// Init done, update the UI after doing locking
					access(new myRunnableClass(num, cell));
					solverThread.sleep(500);

					if (solveSudoko()) {
						Random randomGenerator = new Random();
						solvedSuccessfullyLabel.setValue("Solved the puzzle successfully !!!" + randomGenerator.nextInt(100));
						return true;
					}

					uploadReceiver.getMatrix()[cell.getRowIndex()][cell.getColumnIndex()] = 0; // unassign
					// System.out.println("Backtracking...");
					solvedSuccessfullyLabel.setValue("Back-tracking to cell. . ." + "(" + cell.getRowIndex() + "," + cell.getColumnIndex() + "): " + num);
					solverThread.sleep(500);
					// Item itt = table.getItem(cell.getRowIndex());
					// Property<Integer> rrr =
					// itt.getItemProperty(cell.getColumnIndex());
					// rrr.setValue(num);
					access(new myRunnableClass(0, cell));

				}

			}
			return false;

		}

		/**************************/
		/* Are there any conflicts */
		/**************************/

		private boolean AreThereConflicts(Integer number, Integer row, Integer col) {

			Cube cc = findCube(row, col);
			for (int jj = 0; jj < 9; jj++) {
				if ((number == uploadReceiver.getMatrix()[row][jj] || uploadReceiver.getMatrix()[jj][col] == number)) {
					return true;
				}
			}

			for (int jj = cc.getRowfromIndex(); jj < cc.getRowtoIndex(); jj++) {
				for (int kk = cc.getColfromIndex(); kk < cc.getColtoIndex(); kk++) {
					if ((number == uploadReceiver.getMatrix()[jj][kk] || uploadReceiver.getMatrix()[jj][kk] == number)) {
						return true;
					}
				}
			}

			for (Integer mm : cc.getLeftOutValues()) {
				if (!cc.getLeftOutValues().contains(number)) {
					return true;
				}
			}
			return false;

		}

		private Cube findCube(Integer i, Integer j) {
			for (Cube ll : cubeList) {
				if (i >= ll.getRowfromIndex() && i < ll.getRowtoIndex() && j >= ll.getColfromIndex() && j < ll.getColtoIndex()) {
					return ll;
				}
			}
			return null;
		}

		private Square EmptySquarePresent() {
			Square sq = new Square();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (uploadReceiver.getMatrix()[i][j] == 0) {
						sq.setRowIndex(i);
						sq.setColumnIndex(j);
						sq.setEmptySquare(true);
						return sq;
					}
				}
			}
			return sq;
		}

	}

	/******************************
	 * Initialize the container data
	 ******************************/
	private void createContainerData() {

		// Define the properties (columns)
		// creates columns 0 to 8
		for (int i = 0; i < 9; i++) {
			container.addContainerProperty(i, Integer.class, 0);
		}

		// Add some items
		Object content[][] = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

		int id = 0;
		for (Object[] row : content) {
			// Creates a new Item with the given ID in the Container.
			Item newItem = container.addItem(id);
			// Item newItem = container.getItem(itm);
			id++;

			// Item newItem = container.getItem(container.addItem());
			for (int i = 0; i < 9; i++) {
				// newItem.getItemProperty(0).setValue(row[0]);
				// newItem.getItemProperty(1).setValue(row[1]);
				newItem.getItemProperty(i).setValue(row[i]);
			}

		}

	}

	/*************************/
	/* Creates a Cube */
	/************************/
	private static Cube createCube(CubeTypes ctype) {

		int rowfromIndex = 0, rowtoIndex = 0;
		int colfromIndex = 0, coltoIndex = 0;

		switch (ctype) {

		case TOPLEFT_11:
			rowfromIndex = 0;
			rowtoIndex = 3;
			colfromIndex = 0;
			coltoIndex = 3;
			break;
		case TOPMIDDLE_12:
			rowfromIndex = 0;
			rowtoIndex = 3;
			colfromIndex = 3;
			coltoIndex = 6;
			break;
		case TOPRIGHT_13:
			rowfromIndex = 0;
			rowtoIndex = 3;
			colfromIndex = 6;
			coltoIndex = 9;
			break;

		case MIDDLELEFT_21:
			rowfromIndex = 3;
			rowtoIndex = 6;
			colfromIndex = 0;
			coltoIndex = 3;
			break;
		case MIDDLEMIDDLE_22:
			rowfromIndex = 3;
			rowtoIndex = 6;
			colfromIndex = 3;
			coltoIndex = 6;
			break;
		case MIDDLERIGHT_23:
			rowfromIndex = 3;
			rowtoIndex = 6;
			colfromIndex = 6;
			coltoIndex = 9;
			break;

		case BOTTOMLEFT_31:
			rowfromIndex = 6;
			rowtoIndex = 9;
			colfromIndex = 0;
			coltoIndex = 3;
			break;
		case BOTTOMMIDDLE_32:
			rowfromIndex = 6;
			rowtoIndex = 9;
			colfromIndex = 3;
			coltoIndex = 6;
			break;
		case BOTTOMRIGHT_33:
			rowfromIndex = 6;
			rowtoIndex = 9;
			colfromIndex = 6;
			coltoIndex = 9;
			break;
		default:
			break;
		}

		Set<Integer> h = new HashSet<Integer>();

		for (int i = rowfromIndex; i < rowtoIndex; i++) {
			for (int j = colfromIndex; j < coltoIndex; j++) {
				// System.out.println("Adding :" +
				// uploadReceiver.getMatrix()[i][j]);
				if (uploadReceiver.getMatrix()[i][j] != 0) {
					h.add(uploadReceiver.getMatrix()[i][j]);
				}
			}
		}

		Cube c = new Cube(h);
		c.setCubeName(ctype);
		c.setRowfromIndex(rowfromIndex);
		c.setRowtoIndex(rowtoIndex);
		c.setColfromIndex(colfromIndex);
		c.setColtoIndex(coltoIndex);
		c.cubeValuesNotGiven();
		return c;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}
