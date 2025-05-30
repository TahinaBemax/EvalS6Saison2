import hashlib

def generer_code_produit_simple(nom_produit):
    # Nettoyage du nom
    nom_propre = ''.join(c.lower() for c in nom_produit if c.isalnum())
    
    # Génération du hash MD5
    hash_object = hashlib.md5(nom_propre.encode())
    code = hash_object.hexdigest()[:8].upper()
    
    return code




# Exemple d'utilisation


def set_item_code():
    # Incrémentation du compteur
    _item_counter = 10
    # Formatage du code avec 5 chiffres
    print(f"ITEM-{_item_counter:05d}")

set_item_code()
#produit = "Laptop Gaming XPS"
#code = generer_code_produit_simple(produit)
#print(f"Nom produit: {produit}")
#print(f"Code produit: {code}")