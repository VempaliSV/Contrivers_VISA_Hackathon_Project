from datetime import datetime, date

class RetrievalNo:
    @classmethod
    def No(cls):
        day_of_year = datetime.now().timetuple().tm_yday
        today = str(date.today())
        d=str(datetime.now().hour)
        if len(d)==1:
            d = "0"+d
        return today[3] + str(day_of_year) + d