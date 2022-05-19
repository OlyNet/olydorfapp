from appwrite.client import Client
from appwrite.services.database import Database
import json
import os

client = Client()


# save api key as env var: export AW_S = "[secret api key]"

(client
 .set_endpoint("https://appwrite.960.eu/v1")  # Your API Endpoint
 .set_project("61f3034d10e3bbe97441")  # Your project ID
 .set_key(os.getenv("AW_S"))  # Your secret API key
 )


def upload_bungalows():
    database = Database(client)
    with open('oly.json', 'r') as file:
        data = json.load(file)["objects"]["oly"]["geometries"]

    for bungalow in data:
        addr = bungalow["properties"]["addr"]
        if addr:
            result = database.create_document(
                'bungalows', addr, {"current": None})
            print(result)


if __name__ == "__main__":
    upload_bungalows()
