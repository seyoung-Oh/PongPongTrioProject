import cv2



import numpy as np



import picamera



import threading



import time





import os



import firebase_admin



from firebase_admin import credentials



from firebase_admin import db



from firebase_admin import storage



from uuid import uuid4



import RPi.GPIO as GPIO





PROJECT_ID = "mola-c4bdf"





cred = credentials.Certificate("/home/pi/Downloads/mola-c4bdf-firebase-adminsdk-nivve-ee117b3abc.json")





firebase_admin.initialize_app(cred, {

        'databaseURL': 'https://mola-c4bdf-default-rtdb.firebaseio.com/',

        'storageBucket': "{PROJECT_ID}.appspot.com"

    })



camera = picamera.PiCamera()









def load_yolo():







    net = cv2.dnn.readNet("yolov3.weights", "cfg/yolov3.cfg")







    classes = []







    with open("data/coco.names", "r") as f:







        classes = [line.strip() for line in f.readlines()]















    layers_names = net.getLayerNames()







    output_layers = [layers_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]







    colors = np.random.uniform(0, 255, size=(len(classes), 3))







    return net, classes, colors, output_layers





def load_image(img_path):











    height, width, channels = img_path.shape







    return img_path, height, width, channels





def detect_objects(img, net, outputLayers):







    blob = cv2.dnn.blobFromImage(img, scalefactor=0.00392, size=(320, 320), mean=(0, 0, 0), swapRB=True, crop=False)







    net.setInput(blob)







    outputs = net.forward(outputLayers)







    return blob, outputs





def get_box_dimensions(outputs, height, width):







    boxes = []







    confs = []







    class_ids = []







    for output in outputs:







        for detect in output:







            scores = detect[5:]







            class_id = np.argmax(scores)







            conf = scores[class_id]







            if conf > 0.3:







                center_x = int(detect[0] * width)



                center_y = int(detect[1] * height)



                w = int(detect[2] * width)



                h = int(detect[3] * height)



                x = int(center_x - w / 2)



                y = int(center_y - h / 2)







                boxes.append([x, y, w, h])



                confs.append(float(conf))



                class_ids.append(class_id)







    return boxes, confs, class_ids





def draw_labels(boxes, confs, colors, class_ids, classes, img):







    indexes = cv2.dnn.NMSBoxes(boxes, confs, 0.5, 0.4)







    font = cv2.FONT_HERSHEY_PLAIN







    for i in range(len(boxes)):







        if i in indexes:







            x, y, w, h = boxes[i]







            label = str(classes[class_ids[i]])



            print(label)







            if label == "cat":



                print("find a cat")



                cv2.imwrite('/home/pi/testImage/findacat.jpg', img)



                crop_img = img[y:y+h, x:x+w]



                cv2.imwrite('/home/pi/testImage/catcut.jpg', crop_img)



                catDifference('/home/pi/testImage/catcut.jpg')











            color = colors[i]







            cv2.rectangle(img, (x, y), (x + w, y + h), color, 2)



            cv2.putText(img, label, (x, y - 5), font, 1, color, 1)

            cv2.imwrite('/home/pi/testImage/findacatLabel.jpg', img)








    now = time.ctime()







    print("result "+ now)





def fileUpload(file):







    bucket = firebase_admin.storage.bucket('mola-c4bdf.appspot.com')



    filename = str(file)



    nameList = filename.split('/')



    blob = bucket.blob('image_store/' + nameList[4])





    new_token = uuid4()



    metadata = {"firebaseStorageDownloadTokens": new_token}



    blob.metadata = metadata







    blob.upload_from_filename(filename=file, content_type='image/jpeg')







    print("hello ")



    print(blob.public_url)





def recentFile():







    folder_path = '/home/pi/catImg/'







    each_file_name_and_gen_time = []



    for each_file_name in os.listdir(folder_path):



        each_file_path = folder_path + each_file_name



        each_file_gen_time = os.path.getctime(each_file_path)



        each_file_name_and_gen_time.append(



            (each_file_name, each_file_gen_time)



        )











    most_recent_file = max(each_file_name_and_gen_time, key=lambda x: x[1])[0]

    most_recent_filename = folder_path + str(most_recent_file)





    filename = str(most_recent_file)



    filename = filename.split('.')[0]

    filename = filename.split('_')[1]

    filename = filename.replace(str(filename), str(int(filename) + 1))









    filename = folder_path + "catImg_" + filename + ".jpg"







    return most_recent_filename , filename





def catDifference(image_path):







    recentName, newName = recentFile()



    recentImg = cv2.imread(recentName)



    newImg = cv2.imread(image_path)



    recentImg = cv2.resize(recentImg, dsize=(640, 480), interpolation=cv2.INTER_LINEAR)



    newImg = cv2.resize(newImg, dsize=(640, 480), interpolation=cv2.INTER_LINEAR)



    imgs = [recentImg, newImg]



    hists = []







    for i, img in enumerate(imgs):







        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)



        hist = cv2.calcHist([hsv], [0, 1], None, [180, 256], [0, 180, 0, 256])



        cv2.normalize(hist, hist, 0, 1, cv2.NORM_MINMAX)



        hists.append(hist)











    methods = {'CORREL': cv2.HISTCMP_CORREL, 'CHISQR': cv2.HISTCMP_CHISQR,



               'INTERSECT': cv2.HISTCMP_INTERSECT,



               'BHATTACHARYYA': cv2.HISTCMP_BHATTACHARYYA}







    count = 0







    for j, (name, flag) in enumerate(methods.items()):



        ret = cv2.compareHist(hists[0], hists[1], flag)



        if flag == cv2.HISTCMP_INTERSECT:



            ret = ret / np.sum(hists[0])



            if ret > 0.3:



                count += 1



        if flag == cv2.HISTCMP_CHISQR and ret < 0.7:



            count += 1



        if flag == cv2.HISTCMP_CORREL and ret > 0.6:



            count += 1



        if flag == cv2.HISTCMP_BHATTACHARYYA and ret < 0.8:



            count += 1







    if count >= 3:
	print("same cat")
        waitStop()



    else:

        ref = db.reference().child('catfood').child('rest')

        result = ref.get()

	resNum = int(result['num'])

        if resNum > 0:

            value = resNum - 20

            ref.set({'num': value})



            cv2.imwrite(newName, newImg)

            fileUpload(newName)

            motorStart()

	else :
	    print("no food")

        waitStop()





def waitStop():



    print("waiting...")



    time.sleep(10)



    print("start again")



    image_detect()





def motorStart():





    pin = 16



    GPIO.setmode(GPIO.BOARD)



    GPIO.setup(pin, GPIO.OUT)



    pwm = GPIO.PWM(pin, 50)







    pwm.start(0)

    time.sleep(1)



    pwm.ChangeDutyCycle(4)

    time.sleep(0.3)

    pwm.ChangeDutyCycle(3)
    pwm.ChangeDutyCycle(2)

    time.sleep(1)




    time.sleep(1)





    pwm.stop()



    GPIO.cleanup(pin)





def image_detect():







    camera.capture('/home/pi/testImage/capture.jpg')







    now = time.ctime()







    print("cap " + now)







    img = cv2.imread('/home/pi/testImage/capture.jpg')















    model, classes, colors, output_layers = load_yolo()







    image, height, width, channels = load_image(img)







    blob, outputs = detect_objects(image, model, output_layers)







    boxes, confs, class_ids = get_box_dimensions(outputs, height, width)







    draw_labels(boxes, confs, colors, class_ids, classes, image)























    threading.Timer(2.5, image_detect).start()





image_detect()








