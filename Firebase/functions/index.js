// See readme in top level of Firebase directory for info on how to deploy edits to this file.

// How to read some of these functions:
// Use of {asdf} in file path represents wildcard.
// Functions only run when event occurs on something in filepath matching pattern we provided above.
// .onCreate only responds when an object is created; versus onWrite which runs on create/update/delete.

/*

const functions = require('firebase-functions');

// This function limits the number of observation records retained per beacon.
// Modifed from example at Github: firebase/functions-samples/limit-children
exports.deleteExtraObservations = functions.database.ref('/observations/{tagid}/{timestamp}').onCreate(event => {
	
	// Max number of observations to save. ****ADJUST BEHAVIOR HERE****
	const MAX_LOG_COUNT = 5;

	const parentRef = event.data.ref.parent;
		
	// Removes siblings of the node that triggered the function if there are more than MAX_LOG_COUNT.
	// Firebase conveniently counts the timestamps to keep starting with largest.
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
});

// This function deletes heatmap records that are older than a specified time.
exports.deleteOldHeatmapObservations = functions.database.ref('/heatmap/{timestamp}').onCreate(event => {
	
	// Oldest records to store for heatmap. Format: milliseconds
	const MAX_HEATMAP_OBSERVATION_AGE = 1800000; // 30 minutes

	const parentRef = event.data.ref.parent;
	const parentKey = parentRef.key;

	const currTime = new Date().getTime();
	return parentRef.once('value').then(snapshot => {
		const updates = {};
		snapshot.forEach(function(child) {
			if (child.key < currTime - MAX_HEATMAP_OBSERVATION_AGE) {
				updates[child.key] = null;
			}
		});
		// Update the parent. This effectively removes the extra children.
		return parentRef.update(updates);
	});	
});

*/