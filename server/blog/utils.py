import copy

import pyparsing as pp


class ParseNode:
    def __init__(self, operand, left, right):
        self.operand = operand
        self.left = left
        self.right = right


def create_parse_tree(arr2):
    arr = copy.copy(arr2)
    # print("debug6:  ", arr)

    if isinstance(arr,ParseNode) :
        return arr
    if arr[1] == '==' or arr[1] == '!=':
        return ParseNode(arr[1], arr[0], arr[2])

    while len(arr) != 3:
        # print("debug7 : ", arr)
        arr3 = [ParseNode(arr[1], create_parse_tree(arr[0]), create_parse_tree(arr[2]))]
        arr3.extend(arr[3:])
        arr = arr3
        # print("debug5 : ", arr)

    return ParseNode(arr[1], create_parse_tree(arr[0]), create_parse_tree(arr[2]))


def get_conditional_options(option, value):
    value = int(value)
    permuted = [[0, 0, 0], [0, 0, 1], [0, 1, 0], [0, 1, 1], [1, 0, 0], [1, 0, 1], [1, 1, 0], [1, 1, 1]]
    if option == "option1":
        for i in range(len(permuted)):
            permuted[i].insert(0, value)
        return permuted
    elif option == "option2":
        for i in range(len(permuted)):
            permuted[i].insert(1, value)
        return permuted
    elif option == "option3":
        for i in range(len(permuted)):
            permuted[i].insert(2, value)
        return permuted
    elif option == "option4":
        for i in range(len(permuted)):
            permuted[i].insert(3, value)
        return copy.copy(permuted)


def intersection(arr1, arr2):
    arr = []
    for i in range(len(arr2)):
        if arr2[i] in arr1:
            arr.append(arr2[i])
    # print("and debug ", arr1, "\n        ", arr2, "\n    result ->", arr)

    return arr


def union(arr1, arr2):
    arr = copy.copy(arr1)
    for i in range(len(arr2)):
        if arr2[i] not in arr1:
            arr.append(arr2[i])
    return arr


def calculate(tree):
    if tree.operand == '==':
        return get_conditional_options(tree.left, int(tree.right))
    if tree.operand == "!=":
        return get_conditional_options(tree.left, 1 - int(tree.right))
    if tree.operand == "or":
        return union(calculate(tree.left), calculate(tree.right))
    if tree.operand == "and":
        return intersection(calculate(tree.left), calculate(tree.right))


def parse_query(string):  # test: "where (option1==1 or (option2==1 and option4==1)) and option3 != 0 "

    # preprocess :
    if "where" in string:
        string = string.replace("where", "").strip()

    clause = pp.Group(pp.Word(pp.alphanums)('field') + pp.oneOf('== !=')('operator') + pp.Word(pp.alphanums)('value'))
    statement = pp.infixNotation(clause, [
        ('and', 2, pp.opAssoc.LEFT,),
        ('or', 2, pp.opAssoc.LEFT,)
    ])
    result = statement.parseString(string)
    while len(result) == 1:
        result = result[0]
    # print("debug1 - ", result)
    parse_tree = create_parse_tree(result)
    strings = calculate(parse_tree)
    strings2 = []
    for i in range(len(strings)):
        stri = str(strings[i][0]) + str(strings[i][1]) + str(strings[i][2]) + str(strings[i][3])
        strings2.append(stri)
    return strings2
