package com.bg.ody.client.core;

import com.badlogic.gdx.graphics.Color;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class DynamicLight extends PointLight {
	LightManager lightMan;
	long stamp = 0;
	boolean flickers;
	boolean flicker;
	int flickerLength = 0;
	float distance = 0f;
	long flickerAt = 0;

	public DynamicLight(final LightManager lightMan, final boolean flickers, final RayHandler rayHandler,
			final int rays, final Color color, final float distance, final float x, final float y) {
		super(rayHandler, rays, color, distance, x, y);
		this.flicker = false;
		this.lightMan = lightMan;
		this.flickers = flickers;
		this.distance = distance;
	}

	public void update(long tick) {
		float newDist = 0.0f;
		//if (tick > stamp) {
			//stamp = tick + 200;
			if (flickers && tick > flickerAt) {
				//flicker = !flicker;
				//if (flicker) {
					newDist = distance * (1.8f + (float) (Math.random() / 10.0));
					setDistance(newDist);
				//}
				flickerAt = tick + 100 + (int) (Math.random() * 50.0);
			}
		//}
		super.update();
	}
}
