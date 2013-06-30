package foo;

import java.util.HashSet;
import java.util.Set;

public class Cube {

	private Set<Integer> cubeValues;
	private CubeTypes cubeName;
	private Set<Integer> leftOutValues;
	private Integer rowfromIndex, rowtoIndex, colfromIndex, coltoIndex;

	public Cube(Set<Integer> vals) {
		leftOutValues = new HashSet<Integer>();
		cubeValues = new HashSet<Integer>();
		// System.out.println("VALUES:" + vals.toString());
		cubeValues.addAll(vals);
	}

	public void removeleftOutValue(Integer val) {
		System.out.println("; Removing " + val);
		this.leftOutValues.remove(val);
	}

	public Set<Integer> getCubeValues() {
		return cubeValues;
	}

	@Override
	public String toString() {
		System.out.println(cubeName.toString() + ": " + cubeValues);
		return "";
	}

	public void setCubeName(CubeTypes type) {
		this.cubeName = type;
	}

	public void setCubeValues(Set<Integer> cubeValues) {
		this.cubeValues = cubeValues;
	}

	public CubeTypes getCubeName() {
		return cubeName;
	}

	public Set<Integer> cubeValuesNotGiven() {
		for (int i = 1; i < 10; i++) {
			if (this.cubeValues.add(i)) {
				leftOutValues.add(i);
			}
		}

		return leftOutValues;

	}

	public Set<Integer> getLeftOutValues() {
		return leftOutValues;
	}

	public void setLeftOutValues(Set<Integer> leftOutValues) {
		this.leftOutValues = leftOutValues;
	}

	public Integer getRowfromIndex() {
		return rowfromIndex;
	}

	public void setRowfromIndex(Integer rowfromIndex) {
		this.rowfromIndex = rowfromIndex;
	}

	public Integer getRowtoIndex() {
		return rowtoIndex;
	}

	public void setRowtoIndex(Integer rowtoIndex) {
		this.rowtoIndex = rowtoIndex;
	}

	public Integer getColfromIndex() {
		return colfromIndex;
	}

	public void setColfromIndex(Integer colfromIndex) {
		this.colfromIndex = colfromIndex;
	}

	public Integer getColtoIndex() {
		return coltoIndex;
	}

	public void setColtoIndex(Integer coltoIndex) {
		this.coltoIndex = coltoIndex;
	}
}
