from flask import Flask, request, jsonify
from CR import AnimalShelter

# Initialize Flask
app = Flask(__name__)

# Initialize database connection
username = "aacuser"
password = "SNHU1234"
db = AnimalShelter(username, password)


# Routes

# POST
@app.route('/animals', methods=['POST'])
def create_animal():
    data = request.json
    if data is None:
        return jsonify({"error": "No JSON received"}), 400

    result = db.create(data)
    if result:
        return jsonify({"success": True, "animal": data}), 201
    else:
        return jsonify({"success": False}), 500


# GET
@app.route('/animals', methods=['GET'])
def read_animals():
    query = request.args.to_dict()  # convert ?field=value into a Python dict
    animals = db.read(query)

    for a in animals:
        if "_id" in a:
            a["_id"] = str(a["_id"])

    return jsonify(animals), 200


# PUT
@app.route('/animals/<string:animal_id>', methods=['PUT'])
def update_animal(animal_id):
    data = request.json
    result = db.update({"_id": animal_id}, data)

    if result:
        return jsonify({"success": True}), 200
    return jsonify({"error": "Update failed"}), 400


# DELETE
@app.route('/animals/<string:animal_id>', methods=['DELETE'])
def delete_animal(animal_id):
    result = db.delete({"_id": animal_id})
    if result:
        return jsonify({"success": True}), 200
    return jsonify({"error": "Delete failed"}), 400


# Run the API
if __name__ == '__main__':
    app.run(debug=True, port=5000)