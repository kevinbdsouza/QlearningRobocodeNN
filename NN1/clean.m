LUT = load('LookUpTableVal.txt');

k = 1;
LUTnew = [];

for i=1:size(LUT,1)
    if LUT(i,2) ~= 0
        LUTnew(k,:) = LUT(i,:);
        k = k+1;
    end 
    
end 

fid = fopen('LUTval.txt','wt');

for i = 1:size(LUTnew,1)
    fprintf(fid,'%g\t',LUTnew(i,:));
    fprintf(fid,'\n');
end
fclose(fid);