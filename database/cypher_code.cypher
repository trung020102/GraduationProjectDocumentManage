// Delete all node that has no relationship
MATCH (n:Document)
  WHERE NOT (n)--()
DELETE n