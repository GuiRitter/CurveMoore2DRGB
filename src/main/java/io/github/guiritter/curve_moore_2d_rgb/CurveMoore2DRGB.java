package io.github.guiritter.curve_moore_2d_rgb;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.System.out;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import io.github.guiritter.curve_moore._2d.CurveMoore2D;
import io.github.guiritter.curve_moore._3d.CurveMoore3D;
import io.github.guiritter.fit_linear.FitLinear;

public class CurveMoore2DRGB {

	private static long xMin = Long.MAX_VALUE;
	private static long xMax = Long.MIN_VALUE;

	private static long yMin = Long.MAX_VALUE;
	private static long yMax = Long.MIN_VALUE;

	private static long zMin = Long.MAX_VALUE;
	private static long zMax = Long.MIN_VALUE;

	private static Set<Long> distinctPositionSet = new HashSet<>();

	public static void main(String args[]) throws IOException {

		var colorGenerator = CurveMoore3D.orderZero();
		var positionGenerator = CurveMoore2D.orderZero();

		colorGenerator = colorGenerator.nextOrder().nextOrder().nextOrder().nextOrder().nextOrder().nextOrder();
		positionGenerator = positionGenerator.nextOrder().nextOrder().nextOrder().nextOrder().nextOrder().nextOrder().nextOrder().nextOrder().nextOrder();

		colorGenerator.pointCurrentList.forEach(point -> {
			xMin = Long.min(xMin, point.x);
			xMax = Long.max(xMax, point.x);

			yMin = Long.min(yMin, point.y);
			yMax = Long.max(yMax, point.y);
			
			zMin = Long.min(zMin, point.z);
			zMax = Long.max(zMax, point.z);
		});

		out.println(xMin + "\t" + yMin + "\t" + zMin + "\t" + xMax + "\t" + yMax + "\t" + zMax);

		var fitLinear = new FitLinear(xMin, 0, xMax, 255);

		positionGenerator.pointCurrentList.forEach(point -> {
			distinctPositionSet.add(point.x);
			distinctPositionSet.add(point.y);
		});

		out.println(distinctPositionSet.size());

		var sideSize = distinctPositionSet.size();
		var pathSize = colorGenerator.pointCurrentList.size();

		var image = new BufferedImage(sideSize, sideSize, TYPE_INT_RGB);
		var raster = image.getRaster();
		io.github.guiritter.curve_moore._3d.Point colorPoint;
		var colorArray = new int[3];
		io.github.guiritter.curve_moore._2d.Point positionPoint;
		int x;
		int y;

		for (int index = 0; index < pathSize; index++) { 
			colorPoint = colorGenerator.pointCurrentList.get(index);
			colorArray[0] = (int)Math.round(fitLinear.f(colorPoint.x));
			colorArray[1] = (int)Math.round(fitLinear.f(colorPoint.y));
			colorArray[2] = (int)Math.round(fitLinear.f(colorPoint.z));
			positionPoint = positionGenerator.pointCurrentList.get(index);
			x = (int) ((positionPoint.x - 1) / 2) + (sideSize / 2);
			y = (int) ((positionPoint.y - 1) / 2) + (sideSize / 2);
			raster.setPixel(x, y, colorArray);
		}

		ImageIO.write(image, "png", new File("[redacted]"));
	}
}
