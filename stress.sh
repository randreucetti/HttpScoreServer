for i in {1..100000}
do
	curl -s localhost:8000/$i/login &
done
