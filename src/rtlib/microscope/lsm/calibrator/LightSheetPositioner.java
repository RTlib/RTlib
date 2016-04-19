package rtlib.microscope.lsm.calibrator;

import static java.lang.Math.max;
import static java.lang.Math.min;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class LightSheetPositioner
{

	public DenseMatrix64F mTransformMatrix, mInverseTransformMatrix;

	public LightSheetPositioner()
	{
	}

	public LightSheetPositioner(SimpleMatrix pTransformMatrix)
	{
		mTransformMatrix = pTransformMatrix.getMatrix();
		mInverseTransformMatrix = pTransformMatrix.invert().getMatrix();
	}

	public void setAt(LightSheetMicroscope pLightSheetMicroscope,
										int pLightSheetIndex,
										double pPixelX,
										double pPixelY)
	{
		LightSheetInterface lLightSheetDevice = pLightSheetMicroscope.getDeviceLists()
																																	.getLightSheetDevice(pLightSheetIndex);

		setAt(lLightSheetDevice, pPixelX, pPixelY);
	}

	public void setAt(LightSheetInterface pLightSheetDevice,
										double pPixelX,
										double pPixelY)
	{

		SimpleMatrix lControlVector = getControlVector(pPixelX, pPixelY);

		double lLightSheetX = lControlVector.get(0, 0);
		double lLightSheetY = lControlVector.get(1, 0);

		pLightSheetDevice.getXVariable().set(lLightSheetX);
		pLightSheetDevice.getYVariable().set(lLightSheetY);
	}

	public void illuminateBox(LightSheetInterface pLightSheetDevice,
														double pMinX,
														double pMinY,
														double pMaxX,
														double pMaxY)
	{

		SimpleMatrix lControlVectorA = getControlVector(pMinX, pMinY);
		SimpleMatrix lControlVectorB = getControlVector(pMaxX, pMaxY);
		SimpleMatrix lControlVectorC = getControlVector(pMinX, pMaxY);
		SimpleMatrix lControlVectorD = getControlVector(pMaxX, pMinY);

		double lAX = lControlVectorA.get(0, 0);
		double lAY = lControlVectorA.get(1, 0);

		double lBX = lControlVectorB.get(0, 0);
		double lBY = lControlVectorB.get(1, 0);

		double lCX = lControlVectorC.get(0, 0);
		double lCY = lControlVectorC.get(1, 0);

		double lDX = lControlVectorD.get(0, 0);
		double lDY = lControlVectorD.get(1, 0);

		double lMinLSX = min(min(lAX, lBX), min(lCX, lDX));
		double lMinLSY = min(min(lAY, lBY), min(lCY, lDY));

		double lMaxLSX = max(max(lAX, lBX), max(lCX, lDX));
		double lMaxLSY = max(max(lAY, lBY), max(lCY, lDY));

		double lX = (lMaxLSX + lMinLSX) / 2;
		double lY = (lMaxLSY + lMinLSY) / 2;

		double lWidth = lMaxLSX - lMinLSX;
		double lHeight = lMaxLSY - lMinLSY;

		pLightSheetDevice.getXVariable().set(lX);
		pLightSheetDevice.getYVariable().set(lY);

		pLightSheetDevice.getWidthVariable().set(lWidth);
		pLightSheetDevice.getHeightVariable().set(lHeight);

	}

	private SimpleMatrix getControlVector(double pPixelX, double pPixelY)
	{
		SimpleMatrix lVector = new SimpleMatrix(2, 1);
		lVector.set(0, 0, pPixelX);
		lVector.set(1, 0, pPixelY);

		SimpleMatrix lControlVector = SimpleMatrix.wrap(mInverseTransformMatrix)
																							.mult(lVector);
		return lControlVector;
	}

}