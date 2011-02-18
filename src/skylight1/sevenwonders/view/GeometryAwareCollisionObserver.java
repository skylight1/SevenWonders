package skylight1.sevenwonders.view;

import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.CollisionDetector.CollisionObserver;

public interface GeometryAwareCollisionObserver extends CollisionObserver {
	void addGeometry(OpenGLGeometry anOpenGLGeometry, int anAnimationIndex, int aGeometryIndex);
}
