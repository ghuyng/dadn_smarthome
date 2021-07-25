var admin = require("firebase-admin");

var serviceAccount = require("./dadn-db-e1bc2-firebase-adminsdk-a5x3b-9b001afa3d.json");


admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://dadn-db-e1bc2-default-rtdb.firebaseio.com"
});

module.exports.admin = admin