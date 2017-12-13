// Modifed from example at Github: firebase/functions-samples/limit-children
// See readme in top level of Firebase directory for info on how to deploy edits to this file.

const functions = require('firebase-functions');

// Max number of observations to save. ****ADJUST BEHAVIOR HERE****
const MAX_LOG_COUNT = 5;

// Oldest records to store for heatmap. Format: milliseconds
const MAX_LOG_AGE = 1800000; // 30 minutes

exports.truncate = functions.database.ref('/observations/{tagid}/{timestamp}').onWrite(event => {
	// Use of {asdf} in file path represents wildcard.

	const parentRef = event.data.ref.parent;

	const parentKey = parentRef.key;
	
	const currTime = new Date().getTime();
	
	if (parentKey == 'locations') {	// Pruning unique to the dummy "location" node.
		return parentRef.once('value').then(snapshot => {
			const updates = {};
			snapshot.forEach(function(child) {
				if (child.key < currTime - MAX_LOG_AGE) {
					updates[child.key] = null;
				}
			});
		// Update the parent. This effectively removes the extra children.
		return parentRef.update(updates);
		});

	} else {	// Normal pruing, for all other nodes.
	// Removes siblings of the node that triggered the function if there are more than MAX_LOG_COUNT.
		return parentRef.once('value').then(snapshot => {
			if (snapshot.numChildren() >= MAX_LOG_COUNT) {
				let childCount = 0;
				const updates = {};
				snapshot.forEach(function(child) {
					if (++childCount <= snapshot.numChildren() - MAX_LOG_COUNT) {
						updates[child.key] = null;
					}
				});
			// Update the parent. This effectively removes the extra children.
			return parentRef.update(updates);
			}
		});

	}
		
});
