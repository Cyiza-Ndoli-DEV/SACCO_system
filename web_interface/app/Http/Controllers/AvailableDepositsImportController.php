<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\AvailableDepositsImport;


class AvailableDepositsImportController extends Controller
{
    public function show()
    {
        return view('upload');
    }

    
    public function upload(Request $request)
    {
        $file = $request->file('excel_file');
        Excel::import(new AvailableDepositsImport, $file);

        return back()->withStatus('Excel file imported successfully');

        
    }
}
