package foo;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("sudotheme")
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

	@Override
	protected void init(VaadinRequest request) {

		upload.setImmediate(true);
		upload.setButtonCaption("Upload Sudoko file");

		title.addStyleName("mystyle");
		table.addStyleName("checkerboard");

		createContainerData();
		table.setPageLength(table.size());
		table.setContainerDataSource(container);
		table.setImmediate(true);

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

		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		// upload.setReceiver(uploadReceiver);
		upload.addFinishedListener(uploadReceiver);

		((IndexedContainer) container).addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				// This is all we can get w/o reflection
				Property property = event.getProperty();
				Integer value = (Integer) property.getValue();
				System.out.print("\nValue: " + value);

				Map<String, Object> returnData;

				// Use reflection to get item and property ID
				// got this code from
				// https://github.com/ksnortum/TestContainerInsert/blob/master/README.md
				try {
					returnData = getIdAndProperty(property);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
					return;
				}

				Object itemId = returnData.get("itemId");
				Integer propertyId = (Integer) returnData.get("propertyId");
				System.out.println(", Item ID: " + itemId + ", Property ID: " + propertyId);

				IndexedContainer container = (IndexedContainer) table.getContainerDataSource();
				Item item = container.getItem(itemId);
				Property<Integer> nameProperty = item.getItemProperty(propertyId);
				// nameProperty.setValue((Integer) value);

				// item.getItemProperty(propertyId).setValue(value);

				table.setContainerDataSource(container);
				table.setImmediate(true);
				// try {
				// Thread.sleep(2000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			}

			private Map<String, Object> getIdAndProperty(Property eventProperty) throws NoSuchFieldException {

				Map<String, Object> returnData = new HashMap<String, Object>();
				Class<? extends Property> clazz = eventProperty.getClass();
				final Field idField = clazz.getDeclaredField("itemId");
				final Field propertyField = clazz.getDeclaredField("propertyId");

				AccessController.doPrivileged(new PrivilegedAction<Object>() {
					@Override
					public Object run() {
						idField.setAccessible(true);
						propertyField.setAccessible(true);
						return null;
					}
				});

				try {
					returnData.put("itemId", idField.get(eventProperty));
					returnData.put("propertyId", propertyField.get(eventProperty));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				return returnData;
			}

		});

		/*
		 * Click on the populate button
		 */
		populate.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				statusLabel.setCaption(uploadReceiver.getFileName());
				Object matrix[][] = uploadReceiver.getMatrix();
				int id = 0;
				for (Object[] row : matrix) {
					Item newItem = container.getItem(id);
					id++;
					// Item newItem = container.getItem(container.addItem());
					for (int i = 0; i < 9; i++) {
						newItem.getItemProperty(i).setValue(row[i]);
					}
				}

				// for (int i = 0; i < 9; i++) {
				// for (int j = 0; j < 9; j++) {
				// Item newItem = container.getItem(i);
				// newItem.getItemProperty(j).setValue(matrix[i][j]);
				// }
				// }
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
				solveSudoko();
			}

			/*
			 * Solver
			 */
			private boolean solveSudoko() {

				Square cell = EmptySquarePresent();
				if (!cell.isEmptySquare()) {
					return true;
				}

				/*
				 * Reason why its 1 to 9 is because you want to try numbers from
				 * 1 to 9 in each cell
				 */
				for (int num = 1; num <= 9; num++) {

					// check if no conflicts then
					if (!AreThereConflicts(num, cell.getRowIndex(), cell.getColumnIndex())) {
						uploadReceiver.getMatrix()[cell.getRowIndex()][cell.getColumnIndex()] = num;
						Item row = container.getItem(cell.getRowIndex());
						Property<Integer> col = row.getItemProperty(cell.getColumnIndex());
						col.setValue(num);
						table.setContainerDataSource(container);
						table.setImmediate(true);
						table.refreshRowCache();
						table.requestRepaint();
						table.setContainerDataSource(table.getContainerDataSource());

						if (solveSudoko()) {
							return true;
						}
						uploadReceiver.getMatrix()[cell.getRowIndex()][cell.getColumnIndex()] = 0; // unassign
						Item itt = container.getItem(cell.getRowIndex());
						Property<Integer> rrr = itt.getItemProperty(cell.getColumnIndex());
						rrr.setValue(num);
						table.setContainerDataSource(container);
						table.setImmediate(true);
						table.refreshRowCache();
						table.requestRepaint();
						table.setContainerDataSource(table.getContainerDataSource());

					}

				}
				return false;

			}

			/*
			 * Are there any conflicts ?
			 */
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

			/*
			 * Find cube
			 */

			private Cube findCube(int i, int j) {
				for (Cube ll : cubeList) {
					if (i >= ll.getRowfromIndex() && i < ll.getRowtoIndex() && j >= ll.getColfromIndex() && j < ll.getColtoIndex()) {
						return ll;
					}
				}
				return null;

			}

			/*
			 * Returns an empty square
			 */
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
		});

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

	/*
	 * Creates a Cube
	 */
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
