import hashlib
import json
import math

from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from .models import Data, AdminVariable, OptionsAsStringFrequency
from .utils import *

k = 256
m = 128


def home(request):
    # initialize_cms_matrix(0)
    # initialize_cms_matrix(1)
    # initialize_cms_matrix(2)
    return render(request, 'blog/home.html')

def reset(request):
    initialize_cms_matrix(0)
    initialize_cms_matrix(1)
    initialize_cms_matrix(2)

    total_number_of_records = AdminVariable.objects.filter(name="n")[0]

    total_number_of_records.value = 0
    total_number_of_records.save()


    return HttpResponse("done!")


def hash_func(index, mstr):
    # read the content of the random-hash-strings file
    file = open('./random-hash-strings.txt')

    # read lines
    content = file.readlines()

    # make new string to hash -> index'th line of file + str
    # print(content[index])
    str2hash = mstr + content[index].strip()

    # print("hash input -> ",str2hash)
    # apply MD5 to new string to hash
    # encoding GeeksforGeeks using encode()
    # then sending to md5()
    result = hashlib.md5(str2hash.encode())
    # print("hash string :  ",result.hexdigest())
    # get integer value of hash result
    int_value = int(result.hexdigest(), 16)

    # print("hash test big int --- > " , int_value)

    if int_value < 0:
        int_value = int_value * (-1)

    file.close()

    return int_value % m


def get_vector(str):
    count = 0
    pointer = 0
    result = [0 for i in range(m)]

    while count < 128:
        if str[pointer] == '-':
            result[count] = -1
            pointer += 2
        else:
            result[count] = 1
            pointer += 1

        count += 1

    return result


def get_vector_string(vector):
    result = ""
    for i in range(len(vector)):
        result += str(vector[i]) + ','

    return result[:len(result) - 1]


def write_cms_matrix_to_file(matrix, epsilon_level):
    if epsilon_level == 0 :
        file = open('./matrix.txt', 'w')
    elif epsilon_level == 1:
        file = open('./matrix2.txt', 'w')
    else:
        file = open('./matrix3.txt', 'w')

    lines = []
    for i in matrix:
        lines.append(get_vector_string(i))
        lines.append('\n')

    file.writelines(lines)
    file.close()

    return None


def initialize_cms_matrix(epsilon_level):
    matrix = []
    for i in range(k):
        matrix.append([0 for j in range(m)])
    write_cms_matrix_to_file(matrix, epsilon_level)

    return None


def read_cms_matrix_from_file(epsilon_level):
    if epsilon_level == 0 :
        with open('matrix.txt', 'r') as f:
            l = [[float(num) for num in line.split(',')] for line in f]
    elif epsilon_level == 1:
        with open('matrix2.txt', 'r') as f:
            l = [[float(num) for num in line.split(',')] for line in f]
    else:
        with open('matrix3.txt', 'r') as f:
            l = [[float(num) for num in line.split(',')] for line in f]

    return l


def frequency_estimation(str, epsilon_level):
    matrix = read_cms_matrix_from_file(epsilon_level)

    temp = 0

    for i in range(k):
        temp += matrix[i][hash_func(i, str)]

    n = int(AdminVariable.objects.filter(name="n")[0].value)

    temp = (m / (m - 1)) * ((temp / k) - (n / m))

    return temp


def token(request):
    try:
        # create new data row with defualt values
        user = Data(
            option1=0,
            option2=0,
            option3=0,
            option4=0

        )
        # get t value
        t = AdminVariable.objects.filter(name="t")[0].value
        # epsilon = AdminVariable.objects.filter(name="epsilon")[0].value

        # insert the blog
        user.save()

        response_data = {}
        response_data['t'] = t
        # response_data['epsilon'] = epsilon
        response_data['status'] = '201'

        response = HttpResponse()

        response.content = json.dumps(response_data)
        response['content_type'] = "application/json"
        response['status'] = 201

        return response

    except Exception as e:
        response_data = {}
        response_data['status'] = 'fail'
        response_data['message'] = 'Some error occurred. Please try again. error -> ' + str(e)

        return HttpResponse(json.dumps(response_data), content_type="application/json", status=401)


def need_to_update(request):
    try:
        # check need_to_update value and get t value
        update = AdminVariable.objects.filter(name="need_to_update")[0].value
        t = AdminVariable.objects.filter(name="t")[0].value
        # epsilon = AdminVariable.objects.filter(name="epsilon")[0].value

        response_data = {}
        response_data['t'] = t
        # response_data['epsilon'] = epsilon

        if update == '0':
        # if update == '1':
            response_data['status'] = '201'
            return HttpResponse(json.dumps(response_data), content_type="application/json", status=200)
        else:
            response_data['status'] = '401'
            return HttpResponse(json.dumps(response_data), content_type="application/json", status=200)

    except Exception as e:
        # print("look", e)
        response_data = {}
        response_data['status'] = 'fail'
        response_data['message'] = 'Some error occurred:  ' + e

        return HttpResponse(json.dumps(response_data), content_type="application/json", status=401)


@csrf_exempt
def get_data(request):
    # try:
        # print("hash test --- > " , hash_func(0,"0001"))

        # read request form data
        request_data = request.POST

        my_epsilon = request_data["epsilon"]
        print("My epsilon  -> ", my_epsilon)

        epsilon_level = -1
        if my_epsilon == "3":
            epsilon_level = 0
        elif my_epsilon == "10":
            epsilon_level = 1
        else:
            epsilon_level = 2


        v = []
        j = []
        for i in range(len(request_data)-1):
            # print(request_data[str(i)])

            recieved_value = request_data[str(i)]
            v.append(recieved_value.split('_')[0])
            j.append(recieved_value.split('_')[1])

            print(i, "_v -> ", v[i])
            print(i, "_j -> ", j[i])
        print("----------------------------1")

        matrix = read_cms_matrix_from_file(epsilon_level)

        epsilon = float(my_epsilon)

        c_epsilon = (math.exp(epsilon / 2) + 1) / (math.exp(epsilon / 2) - 1)

        a = c_epsilon / 2

        # add each record vector to the cms matrix
        for i in range(len(v)):
            vector = get_vector(v[i])
            j_index = int(j[i])
            for z in range(m):
                matrix[j_index][z] += k * a * vector[z] + 128

        write_cms_matrix_to_file(matrix, epsilon_level)

        total_number_of_records = AdminVariable.objects.filter(name="n")[0]

        n = int(total_number_of_records.value)

        n += len(v)

        total_number_of_records.value = n
        total_number_of_records.save()

        # request.session['total_number_of_records'] = counter

        response_data = {}
        response_data['status'] = 'ok'

        return HttpResponse(json.dumps(response_data), content_type="application/json", status=201)

    # except Exception as e:
        print("look", e)
        response_data = {}
        response_data['status'] = 'fail'
        response_data['message'] = 'Some error occurred:  ' + str(e)

        return HttpResponse(json.dumps(response_data), content_type="application/json", status=401)


def update_estimated_frequency_table(request, epsilon_level):
    possible_permutations_frequency_dict = {
        "0000": frequency_estimation("0000", epsilon_level),
        "0001": frequency_estimation("0001", epsilon_level),
        "0010": frequency_estimation("0010", epsilon_level),
        "0011": frequency_estimation("0011", epsilon_level),
        "0100": frequency_estimation("0100", epsilon_level),
        "0101": frequency_estimation("0101", epsilon_level),
        "0110": frequency_estimation("0110", epsilon_level),
        "0111": frequency_estimation("0111", epsilon_level),
        "1000": frequency_estimation("1000", epsilon_level),
        "1001": frequency_estimation("1001", epsilon_level),
        "1010": frequency_estimation("1010", epsilon_level),
        "1011": frequency_estimation("1011", epsilon_level),
        "1100": frequency_estimation("1100", epsilon_level),
        "1101": frequency_estimation("1101", epsilon_level),
        "1110": frequency_estimation("1110", epsilon_level),
        "1111": frequency_estimation("1111", epsilon_level)
    }
    # options_dict = {
    # "option1": ["1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"],
    # "option2": ["0100", "0101", "0110", "0111", "1100", "1101", "1110", "1111"],
    # "option3": ["0010", "0011", "0110", "0111", "1010", "1011", "1110", "1111"],
    # "option4": ["0001", "0011", "0101", "0111", "1001", "1011", "1101", "1111"],
    # }

    # option1_freq = sum(possible_permutations_frequency_dict(i) for i in options_dict["option1"])
    # option2_freq = sum(possible_permutations_frequency_dict(i) for i in options_dict["option2"])
    # option3_freq = sum(possible_permutations_frequency_dict(i) for i in options_dict["option3"])
    # option4_freq = sum(possible_permutations_frequency_dict(i) for i in options_dict["option4"])

    OptionsAsStringFrequency.objects.all().delete()

    for s in possible_permutations_frequency_dict:
        print("s - > ", s, "f = ", possible_permutations_frequency_dict[s])
        temp_entry = OptionsAsStringFrequency(s, possible_permutations_frequency_dict[s])
        temp_entry.save()

    return HttpResponse("Estimated frequency table updated successfully!")


@csrf_exempt
def get_estimated_frequency(request):
    try:
        #received_json_data = json.loads(request.body)

        #strings = parse_query(received_json_data['string'])
        epsilon = str(request.POST['epsilon'])

        epsilon_level = -1
        if epsilon == "3":
            epsilon_level = 0
        elif epsilon == "10":
            epsilon_level = 1
        else:
            epsilon_level = 2

        strings = parse_query(str(request.POST['query']))
        

        response_data = {}
        freq = 0
        for i in range(len(strings)):
            freq += frequency_estimation(strings[i], epsilon_level)
        
        response_data["estimated_frequency"] = str(freq)

        response = HttpResponse()

        response.content = json.dumps(response_data)
        response['content_type'] = "application/json"
        # response['status'] = 201
        #return response

        # n = int(AdminVariable.objects.filter(name="n")[0].value)
        # if n != 0:
        #     freq = freq / n
            
        context = {
        'result': [freq],
        }

        return render(request, 'blog/home.html', context)

    except Exception as e:
        print("look", e)
        response_data = {}
        response_data['status'] = 'fail'
        response_data['message'] = 'Some error occurred:  ' + str(e)

        context = {
        'error': ['Some error occurred:  ' + str(e)],
        }
        return render(request, 'blog/home.html', context)

        return HttpResponse(json.dumps(response_data), content_type="application/json", status=401)
