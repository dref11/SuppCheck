import requests
import pandas as pd
from bs4 import BeautifulSoup

page = requests.get('https://www.bestpricenutrition.com/products/vpx-bang-12-per-case-16-fl-oz')
soup = BeautifulSoup(page.text, 'html.parser')

ingredient_name=[]

ingredient_name_list = soup.find("div", "description").find_all("p")

for ingredient in ingredient_name_list:
    ingredient_name.append(ingredient.get_text(strip = True))

df = pd.DataFrame({"ingredient_name":ingredient_name})
df.to_csv('ingredient_name.txt')

print(ingredient_name)
