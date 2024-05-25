//Create simple document that has relationship with sub-category
match (o:Category)
  where id(o) = 51
create (o)-[:CONTAINS_DOCUMENT]->(i:Document {number_sign: '02/2024/TT-BGDĐT'})
return *
