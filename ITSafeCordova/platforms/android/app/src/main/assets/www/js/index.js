
var database = null;
var code = [9875,12368,12347,23561,98741,96321,9852,98521,47896321,4563,36547];
var info = null;

function onDeviceReady() {
	initDatabase();

	cordova.plugins.notification.local.schedule({
		title: 'My first notification',
		text: 'Thats pretty easy...',
		foreground: true
	});
	
}

function alertDismissed() {
    // do something
}

function initDatabase() {
	database = window.sqlitePlugin.openDatabase(
		{
			name: 'itsafe.db',
			location: 'default',
			androidDatabaseProvider: 'system'
		}
	);

	code = code[Math.floor(Math.random()*code.length)];

	database.transaction(function(transaction) {
		// Get the code from database
		transaction.executeSql('SELECT pin FROM code', [], function(tx, rs) {
			console.debug(rs.rows.item(0).pin);
			code = rs.rows.item(0).pin;
		}, function(tx, error) {

			// If there is no code create one
			transaction.executeSql('CREATE TABLE IF NOT EXISTS code (pin)');
			transaction.executeSql('INSERT INTO code VALUES (?)', [code]);

			return false;
		});

	}, function(error) {
		console.debug('Transaction ERROR: ' + error.message);
	}, function() {
		console.debug('Populated database OK');
	});

}

function logError(error) {
	console.log(error);
}

$(document).on("deviceready", onDeviceReady);
