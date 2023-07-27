<?php

namespace App\Http\Controllers;

use App\Models\Reference;

class ReferenceController extends Controller
{
    public function displayData()
    {
        // Assuming you have a "Reference" model and you want to fetch data from it
        $references = Reference::all();

        return view('pages.table', compact('references'));
    }
    
}


