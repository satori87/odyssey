/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.bg.bearplane.ai;

import com.badlogic.gdx.utils.Array;
import com.bg.ody.server.Map;
import com.bg.ody.shared.MapData;
import com.bg.ody.shared.Shared;

/**
 * A random generated graph representing a flat tiled map.
 * 
 * @author davebaol
 */
public class FlatTiledGraph implements TiledGraph<FlatTiledNode> {

	Array<FlatTiledNode> nodes;

	Map map;

	public FlatTiledGraph(Map map) {
		this.map = map;
		this.nodes = new Array<FlatTiledNode>(Shared.MAP_WIDTH * Shared.MAP_WIDTH);
	}

	public void init() {
		nodes.clear();
		int x = 0;
		int y = 0;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				nodes.add(map.tile[x][y]);
			}
		}
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			// int idx = x * Shared.MAP_WIDTH;
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				// FlatTiledNode n = nodes.get(idx + y);
				map.tile[x][y].connections.clear();
				// if (y > 0) {
				addConnection(map.tile[x][y], 0, 0, -1);
				// } else {
				// addConnection(map.tile[x][y], 0, 0, -1);
				// }
				// if (y < Shared.MAP_WIDTH - 1) {
				addConnection(map.tile[x][y], 1, 0, 1);
				// } else {
				// addConnection(map.tile[x][y], 1, 1, 0);
				// }
				// if (x > 0) {
				addConnection(map.tile[x][y], 2, -1, 0);
				// } else {
				// addConnection(map.tile[x][y], 2, -1, 0);
				// }
				// if (x < Shared.MAP_WIDTH - 1) {
				addConnection(map.tile[x][y], 3, 1, 0);
				// } else {
				// addConnection(map.tile[x][y], 3, 1, 0);
				// }

				// map.tile[x][y].vconn = new Array<DefaultConnection<FlatTiledNode>>();
			}
		}

		// update();
	}

	public void update() {
		int x = 0;
		int y = 0;
		//int d = 0;
		for (x = 0; x < Shared.MAP_WIDTH; x++) {
			for (y = 0; y < Shared.MAP_WIDTH; y++) {
				if (y > 0) {
					updateConnection(map.tile[x][y], 0, 0, -1);
				}
				if (y < Shared.MAP_WIDTH - 1) {
					updateConnection(map.tile[x][y], 1, 0, 1);
				}
				if (x > 0) {
					updateConnection(map.tile[x][y], 2, -1, 0);
				}
				if (x < Shared.MAP_WIDTH - 1) {
					updateConnection(map.tile[x][y], 3, 1, 0);
				}
				// map.tile[x][y].vconn.clear();
				// for(DefaultConnection<FlatTiledNode> dc : map.tile[x][y].connections) {
				// if(dc.valid) {
				// map.tile[x][y].vconn.add(dc);
				// }
				// }
			}
		}
	}

	@Override
	public FlatTiledNode getNode(int x, int y) {
		return nodes.get(x * Shared.MAP_WIDTH + y);
	}

	@Override
	public FlatTiledNode getNode(int index) {
		return nodes.get(index);
	}

	@Override
	public int getIndex(FlatTiledNode node) {
		return node.getIndex();
	}

	@Override
	public int getNodeCount() {
		return nodes.size;
	}

	static FlatTiledConnection ftc;
	static FlatTiledNode t;

	@Override
	public Array<DefaultConnection<FlatTiledNode>> getConnections(FlatTiledNode fromNode) {
		return fromNode.getConnections();
	}

	static int xo = 0;
	static int yo = 0;
	static int c = 0;

	private void addConnection(FlatTiledNode n, int d, int xOffset, int yOffset) {
		xo = n.x + xOffset;
		yo = n.y + yOffset;
		if (MapData.inBounds(xo, yo)) {
			t = getNode(n.x + xOffset, n.y + yOffset);
			// if (map.isVacantWalls(t.x, t.y, d, n.x, n.y) && map.isVacantTile(t.x, t.y)) {
			n.connections.add(new FlatTiledConnection(d, this, n, t, 1f));
		} else {
			ftc = new FlatTiledConnection(d, this, n, null, 1f);
			ftc.valid = false;
			n.connections.add(ftc);
		}

		// }
	}

	private void updateConnection(FlatTiledNode n, int d, int xOffset, int yOffset) {
		if (map.isVacantWalls(map.tile[n.x + xOffset][n.y + yOffset].x, map.tile[n.x + xOffset][n.y + yOffset].y, d,
				n.x, n.y)
				&& map.isVacantTile(map.tile[n.x + xOffset][n.y + yOffset].x,
						map.tile[n.x + xOffset][n.y + yOffset].y)) {
			if (map.isVacantElse(map.tile[n.x + xOffset][n.y + yOffset].x, map.tile[n.x + xOffset][n.y + yOffset].y)) {
				n.connections.get(d).valid = true;
				n.connections.get(d).cost = 1f;
			} else {
				n.connections.get(d).valid = true;
				n.connections.get(d).cost = 200f;
			}
		} else {
			n.connections.get(d).cost = 9999f;
			n.connections.get(d).valid = false;
		}
	}

}
