for i in {1..10000}
do
num=$(($RANDOM%1000))
	curl -s localhost:8000/$num/login &
done
